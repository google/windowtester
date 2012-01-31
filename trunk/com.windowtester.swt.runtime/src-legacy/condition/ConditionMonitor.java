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
package com.windowtester.swt.condition;

import com.windowtester.internal.debug.LogHandler;

/**
 * A condition monitor checks for registered conditions and activates their associated
 * handlers. See {@link com.windowtester.swt.condition.IConditionMonitor} for more
 * information.
 * <p>
 * This class is DEPRECATED and exists for backward compatibility. It wrappers and
 * forwards method calls to the *real*
 * {@link com.windowtester.internal.runtime.condition.ConditionMonitor} singleton.
 * 
 * @author Phil Quitslund
 * @author Dan Rubel
 * @deprecated Use {@link com.windowtester.runtime.condition.IConditionMonitor} instead
 */
public class ConditionMonitor
	implements IConditionMonitor
{
	
	/**
	 * Singleton
	 */
	private static final ConditionMonitor INSTANCE = new ConditionMonitor();

	/**
	 * Singleton
	 */
	private ConditionMonitor() {
	}

	/**
	 * Answer the condition monitor
	 * 
	 * @return the monitor (not <code>null</code>)
	 */
	public static ConditionMonitor getInstance() {
		return INSTANCE;
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
	 * @param handler the handler to be activated if the condition is satisified
	 */
	public void add(ICondition condition, final IHandler handler) {
		if (condition == null || handler == null)
			throw new IllegalArgumentException("Arguments cannot be null");
		
		// wrapper the old handler in a new handler before passing it on to the new
		// condition monitor...
		com.windowtester.internal.runtime.condition.ConditionMonitor.getInstance().add(condition,
			new com.windowtester.runtime.condition.IHandler() {
				public void handle(com.windowtester.runtime.IUIContext ui) throws Exception {
					handler.handle((com.windowtester.swt.IUIContext) ui);
				}
			});
	}

	/**
	 * @see com.windowtester.swt.condition.IConditionMonitor#add(com.windowtester.swt.condition.IConditionHandler)
	 */
	public void add(IConditionHandler conditionhandler) {
		add(conditionhandler, conditionhandler);
	}

	/**
	 * @see com.windowtester.swt.condition.IConditionMonitor#removeAll()
	 */
	public void removeAll() {
		com.windowtester.internal.runtime.condition.ConditionMonitor.getInstance().removeAll();
	}

	/**
	 * Remove this condition and it's associated handler from the monitor.
	 * 
	 * @param condition the condition to remove (not <code>null</code>)
	 */
	public void removeHandler(ICondition condition) {
		com.windowtester.internal.runtime.condition.ConditionMonitor.getInstance().removeHandler(condition);
	}

	/**
	 * Get a copy of this monitor's conditions. (Note this method is Non-API.)
	 * 
	 * @return a copy of this monitors condition key-set
	 */
	public com.windowtester.runtime.condition.ICondition[] getConditions() {
		return com.windowtester.internal.runtime.condition.ConditionMonitor.getInstance().getConditions();
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Processing
	//
	// //////////////////////////////////////////////////////////////////////////

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
	public int process(com.windowtester.swt.IUIContext ui) {
		if (ui instanceof com.windowtester.runtime.IUIContext) {
			com.windowtester.runtime.condition.IConditionMonitor monitor = (com.windowtester.runtime.condition.IConditionMonitor) ui
				.getAdapter(com.windowtester.runtime.condition.IConditionMonitor.class);
			return monitor.process((com.windowtester.runtime.IUIContext) ui);
		}
		LogHandler.log(getClass().getName() + " failed to process conditions for " + ui.getClass().getName());
		return PROCESS_NONE;
	}
}