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
package com.windowtester.runtime.swt.condition.shell;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.condition.IHandler;

/**
 * A specialized condition monitor for checking shell related conditions such as whether
 * or not a particular shell is top most so that an associated handler can be called to
 * deal with that shell. These shell conditions are called at least once when a new shell
 * appears, and possibly more frequently.
 * <p>
 * Conditions are added by obtaining this interface from the IUIContext. The
 * {@link IShellCondition#test(Shell)} method is called on the UI thread, while the
 * {@link IHandler#handle(IUIContext)} method is called on the WindowTester test thread.
 * Accessing widgets in the {@link IHandler#handle(IUIContext)} method must be through
 * the IUIContext interface or using
 * {@link org.eclipse.swt.widgets.Display#syncExec(java.lang.Runnable)}. For example, you
 * can add a condition and handler using {@link ShellCondition} or as shown below with
 * your own shell condition:
 * 
 * <pre>
 *                  public void testMain() throws Exception {
 *                      IUIContext ui = getUI();
 *                      IShellMonitor sm = ui.getAdapter(IShellMonitor.class);
 *                      sm.addHandler(
 *                          new IShellCondition() {
 *                              public boolean test(Shell shell) {
 *                                  ... test some condition here ...
 *                              }
 *                          },
 *                          new IHandler() {
 *                              public void handle(IUIContext ui) {
 *                                  ... take some actions here ...
 *                              }
 *                          }
 *                      );
 *                      ...
 * </pre>
 * 
 */
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
	 * Remove all the registered shell handlers from this monitor.
	 */
	void removeAll();
}