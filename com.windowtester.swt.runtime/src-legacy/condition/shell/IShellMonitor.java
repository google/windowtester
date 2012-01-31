package com.windowtester.swt.condition.shell;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.swt.condition.IHandler;

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
public interface IShellMonitor
{
	/**
	 * Add a handler that is called when a dialog matching the specified condition becomes
	 * visible. The condition is called on the UI thread and the handler is called on the
	 * WindowTester test.
	 * 
	 * @param condition the condition to match (not <code>null</code>)
	 * @param handler the handler to be called (not <code>null</code>)
	 */
	void add(IShellCondition condition, IHandler handler);

	/**
	 * Add a handler that is called when a dialog matching the specified condition becomes
	 * visible. The handler's <code>test</code> method is called on the UI thread and the 
	 * <code>handle</code> is called on the WindowTester test.
	 * 
	 * @param conditionHandler the condition handler to match (not <code>null</code>)
	 */
	void add(IShellConditionHandler conditionHandler);
	
	/**
	 * Remove the handler registered to handle the given shell condition from this monitor. If 
	 * no handler is registered to this condition, no action is taken.
	 * 
	 * @param condition the condition whose handler to remove (not <code>null</code>)
	 */
	void remove(IShellCondition condition);
	
	/**
	 * Remove all the registered handlers from this monitor.
	 */
	void removeAll();
}