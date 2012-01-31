package com.windowtester.swt.condition.shell;

import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.runtime.condition.ICondition;

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
public class ShellMonitor
	implements IShellMonitor
{
	/**
	 * Singleton
	 */
	private static final ShellMonitor INSTANCE = new ShellMonitor();

	/**
	 * Singleton
	 */
	private ShellMonitor() {
	}

	/**
	 * Answer the shell monitor
	 * 
	 * @return the monitor (not <code>null</code>)
	 */
	public static ShellMonitor getInstance() {
		return INSTANCE;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	// //////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.swt.condition.shell.IShellMonitor#addHandler(com.windowtester.swt.condition.shell.IShellCondition,
	 *      com.windowtester.swt.condition.IHandler)
	 */
	public void add(IShellCondition condition, final com.windowtester.swt.condition.IHandler handler) {
		add(condition, new com.windowtester.runtime.condition.IHandler() {
			public void handle(com.windowtester.runtime.IUIContext ui) {
				handler.handle((com.windowtester.swt.IUIContext) ui);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.swt.condition.shell.IShellMonitor#addHandler(com.windowtester.swt.condition.shell.IShellCondition,
	 *      com.windowtester.runtime2.condition.IHandler)
	 */
	public void add(IShellCondition condition, com.windowtester.runtime.condition.IHandler handler) {
		ConditionMonitor.getInstance().add(new ShellConditionAdapter(condition), handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.swt.condition.shell.IShellMonitor#add(com.windowtester.swt.condition.shell.IShellConditionHandler)
	 */
	public void add(final IShellConditionHandler conditionhandler) {
		ConditionMonitor.getInstance().add(new ShellConditionAdapter(conditionhandler), new com.windowtester.runtime.condition.IHandler() {
			public void handle(com.windowtester.runtime.IUIContext ui) {
				conditionhandler.handle((com.windowtester.swt.IUIContext) ui);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.swt.condition.shell.IShellMonitor#removeAll()
	 */
	public void removeAll() {
		// FIXME: should not remove ALL handlers, just shell handlers!
		ConditionMonitor.getInstance().removeAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.swt.condition.shell.IShellMonitor#remove(com.windowtester.swt.condition.shell.IShellCondition)
	 */
	public void remove(IShellCondition condition) {
		if (condition == null)
			throw new IllegalArgumentException("Condition must not be null");
		ICondition[] conditions = ConditionMonitor.getInstance().getConditions();
		for (int i = 0; i < conditions.length; i++) {
			if (conditions[i] instanceof ShellConditionAdapter) {
				if (((ShellConditionAdapter) conditions[i]).getCondition() == condition)
					ConditionMonitor.getInstance().removeHandler(conditions[i]);
			}
		}
	}

}
