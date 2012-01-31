/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.internal.runtime.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.condition.IConditionMonitor;
import com.windowtester.runtime.condition.IHandler;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.internal.condition.IConditionWithIdle;

/**
 * A condition monitor checks for registered conditions and activates their associated
 * handlers. See {@link com.windowtester.runtime.condition.IConditionMonitor} for more
 * information.
 */
public class ConditionMonitor
	implements IConditionMonitor
{
	/**
	 * Global condition monitor
	 */
	private static final ConditionMonitor ROOT = new ConditionMonitor(null);

	/**
	 * Flag indicating whether conditions are currently being processed and to prevent
	 * recursive checking of conditions. Synchronize against this {@link #_mappings}
	 * before accessing this field.
	 */
	private boolean _isProcessing = false;

	/**
	 * A list of {@link ICondition} / {@link IHandler} pairs maintained and processed by
	 * the receiver. Synchronize against this field before accessing either this field or
	 * {@link #_isProcessing}.
	 */
	private final List _mappings = new ArrayList();

	/**
	 * This field is used to cache a copy of the {@link #_mappings} field when processing
	 * conditions and associated handlers, and set to <code>null</code> when the cache
	 * is invalidated by accessor methods such as {@link #add(ICondition, IHandler)}.
	 * Synchronize against {@link #_mappings} before accessing this field.
	 */
	private ConditionMapping[] _cachedMappings = null;

	/**
	 * The parent condition monitor or <code>null</code> if none
	 */
	private final IConditionMonitor _parent;

	/**
	 * Construct a new instance that wrappers the specified parent condition monitor.
	 * NOTE: Callers should not access this method directly, but rather use either
	 * {@link #getInstance()} to obtain the global condition monitor or obtain the
	 * condition monitor local to a particular {@link IUIContext} via
	 * {@link IUIContext#getAdapter(Class)} by passing {@link IConditionMonitor} as the
	 * argument.
	 * 
	 * @param parent the parent condition monitor or <code>null</code> if none
	 */
	public ConditionMonitor(IConditionMonitor parent) {
		_parent = parent;
	}

	/**
	 * Answer the global condition monitor. Condition monitors local to a particular
	 * {@link IUIContext} can be accessed via {@link IUIContext#getAdapter(Class)}
	 * by passing {@link IConditionMonitor} as the argument.
	 * 
	 * @return the global monitor (not <code>null</code>)
	 */
	public static ConditionMonitor getInstance() {
		return ROOT;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Add the specified condition and associated handler to the receiver so that it is
	 * included the next time that conditions are processed. WARNING! No checking is
	 * performed to prevent condition/handler pairs from being added multiple times.
	 * 
	 * @param condition the condition to be tested (not <code>null</code>)
	 * @param handler the handler to be activated if the condition is satisfied
	 */
	public void add(ICondition condition, IHandler handler) {
		if (condition == null || handler == null)
			throw new IllegalArgumentException("Arguments cannot be null");
		synchronized (_mappings) {
			_mappings.add(new ConditionMapping(condition, handler));
			_cachedMappings = null;
		}
	}

	/**
	 * @see IConditionMonitor#add(com.windowtester.runtime.condition.IConditionHandler)
	 */
	public void add(IConditionHandler conditionhandler) {
		add(conditionhandler, conditionhandler);
	}

	/**
	 * @see IConditionMonitor#removeAll()
	 */
	public void removeAll() {
		synchronized (_mappings) {
			_mappings.clear();
			_cachedMappings = null; // invalidate cache
		}
	}

	/**
	 * Remove this condition and it's associated handler from the monitor.
	 * 
	 * @param condition the condition to remove (not <code>null</code>)
	 */
	public void removeHandler(ICondition condition) {
		if (condition == null)
			throw new IllegalArgumentException("Condition cannot be null");
		synchronized (_mappings) {
			ConditionMapping mapping = findMapping(condition);
			if (mapping == null)
				return;
			_mappings.remove(mapping);
			_cachedMappings = null; // invalidate cache
		}
	}

	/**
	 * Get a copy of this monitor's conditions. (Note this method is Non-API.)
	 * 
	 * @return a copy of this monitors condition key-set
	 */
	public ICondition[] getConditions() {
		List conditions = new ArrayList();
		ConditionMapping mapping;
		synchronized (_mappings) {
			for (Iterator iter = _mappings.iterator(); iter.hasNext();) {
				mapping = (ConditionMapping) iter.next();
				conditions.add(mapping.condition);
			}
		}
		return (ICondition[]) conditions.toArray(new ICondition[]{});
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Processing
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Utility method for testing conditions. If the specified condition is an instance
	 * of {@link IUICondition} then {@link IUICondition#testUI(IUIContext)} is called
	 * rather than {@link ICondition#test()}.
	 * 
	 * @param ui the UI context (not <code>null</code>)
	 * @param condition the condition to be tested
	 * @return <code>true</code> if the condition is true, else <code>false</code>
	 */
	public static boolean test(IUIContext ui, ICondition condition) {
		if (condition instanceof IUICondition)
			return ((IUICondition) condition).testUI(ui);
		if (condition instanceof IConditionWithIdle)
			return ((ICondition)condition).test();
		return condition.test();
	}

	/**
	 * Process all condition/handler pairs by checking each condition and calling the
	 * associated handlers for any conditions that are satisfied. Nested calls to this
	 * method return immediately without taking any action.
	 * 
	 * @param ui the UIContext instance for use in condition handling
	 * @return one of the following flags indicating what was processed:
	 *         {@link #PROCESS_NONE} if conditions were processed but no conditions were
	 *         satisfied, {@link #PROCESS_ONE_OR_MORE} if conditions were processed and
	 *         at least on condition was satisfied, {@link #PROCESS_RECURSIVE} if
	 *         conditions were already being processed and no additional action was taken.
	 */
	public int process(IUIContext ui) {

		// Prevent recursion and make a copy of the current condition mappings

		ConditionMapping[] copyOfMappings;
		synchronized (_mappings) {
			if (_isProcessing)
				return PROCESS_RECURSIVE;
			_isProcessing = true;
			if (_cachedMappings == null)
				_cachedMappings = (ConditionMapping[]) _mappings.toArray(new ConditionMapping[_mappings.size()]);
			copyOfMappings = _cachedMappings;
		}

		// Iterate over the current mappings and ensure that _isProcessing is reset

		boolean match = false;
		try {
			for (int i = 0; i < copyOfMappings.length; i++) {
				if (test(ui, copyOfMappings[i].condition)) {
					TraceHandler.trace(IRuntimePluginTraceOptions.CONDITIONS, "calling handle: "
						+ copyOfMappings[i].handler);
					try {
						copyOfMappings[i].handler.handle(ui);
						match = true;
					}
					catch (Exception e) {
						Logger.log("Unexpected exception when processing conditions: " + e, e);
					}
				}
			}
			if (_parent != null && _parent.process(ui) == PROCESS_ONE_OR_MORE)
				match = true;
		}
		finally {
			synchronized (_mappings) {
				_isProcessing = false;
			}
		}
		return match ? PROCESS_ONE_OR_MORE : PROCESS_NONE;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Utilities
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Internal class associating a {@link com.windowtester.runtime.condition.ICondition} with
	 * a {@link com.windowtester.runtime.condition.IHandler}
	 */
	private static class ConditionMapping
	{
		final ICondition condition;
		final IHandler handler;

		public ConditionMapping(ICondition c, IHandler h) {
			condition = c;
			handler = h;
		}
	}

	/**
	 * Find the mapping associated with this condition (or <code>null</code> if no
	 * associated mapping can be found.
	 */
	private ConditionMapping findMapping(ICondition condition) {
		ConditionMapping mapping;
		synchronized (_mappings) {
			for (Iterator iter = _mappings.iterator(); iter.hasNext();) {
				mapping = (ConditionMapping) iter.next();
				if (mapping.condition == condition)
					return mapping;
			}
		}
		return null;
	}
}
