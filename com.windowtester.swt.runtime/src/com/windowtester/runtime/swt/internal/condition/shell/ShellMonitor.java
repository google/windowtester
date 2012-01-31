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
package com.windowtester.runtime.swt.internal.condition.shell;

import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IHandler;
import com.windowtester.runtime.swt.condition.shell.IShellCondition;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;

/**
 * A specialized condition monitor for checking shell related conditions such as whether
 * or not a particular shell is top most so that an associated handler can be called to
 * deal with that shell. See {@link IShellMonitor} for more information.
 * 
 * @deprecated Use {@link ConditionMonitor} instead
 */
public class ShellMonitor
	implements IShellMonitor
{
	/**
	 * Global shell monitor
	 */
	private static final ShellMonitor ROOT = new ShellMonitor(ConditionMonitor.getInstance());
	
	/**
	 * The condition monitor utilized by the receiver.
	 */
	private final ConditionMonitor _conditionMonitor;

	/**
	 * Construct a new shell monitor that wrappers the specified condition monitor.
	 * 
	 * @param conditionMonitor the condition monitor (not null)
	 */
	public ShellMonitor(ConditionMonitor conditionMonitor) {
		if (conditionMonitor == null)
			throw new IllegalArgumentException();
		this._conditionMonitor = conditionMonitor;
	}

	/**
	 * Answer the shell monitor
	 * 
	 * @return the monitor (not <code>null</code>)
	 */
	public static ShellMonitor getInstance() {
		return ROOT;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	// //////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.runtime.swt.condition.shell.IShellMonitor#add(com.windowtester.runtime.swt.condition.shell.IShellCondition,
	 *      com.windowtester.runtime.condition.IHandler)
	 */
	public void add(IShellCondition condition, IHandler handler) {
		_conditionMonitor.add(new ShellConditionAdapter(condition), handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.runtime.swt.condition.shell.IShellMonitor#add(com.windowtester.runtime.swt.condition.shell.IShellConditionHandler)
	 */
	public void add(final IShellConditionHandler conditionhandler) {
		_conditionMonitor.add(new ShellConditionAdapter(conditionhandler), new IHandler() {
			public void handle(IUIContext ui) throws Exception {
				conditionhandler.handle(ui);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.swt.condition.shell.IShellMonitor#removeAll()
	 */
	public void removeAll() {
		ICondition[] conditions = _conditionMonitor.getConditions();
		for (int i = 0; i < conditions.length; i++) {
			if (conditions[i] instanceof ShellConditionAdapter)
				_conditionMonitor.removeHandler(conditions[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.swt.condition.shell.IShellMonitor#remove(com.windowtester.swt.condition.shell.IShellCondition)
	 */
	public void remove(IShellCondition condition) {
		if (condition == null)
			throw new IllegalArgumentException("Condition must not be null");
		ICondition[] conditions = _conditionMonitor.getConditions();
		for (int i = 0; i < conditions.length; i++) {
			if (conditions[i] instanceof ShellConditionAdapter) {
				if (((ShellConditionAdapter) conditions[i]).getCondition() == condition)
					_conditionMonitor.removeHandler(conditions[i]);
			}
		}
	}

}
