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
package com.windowtester.runtime.condition;

import com.windowtester.runtime.IUIContext;

/**
 * A condition monitor checks for registered conditions and activates their associated
 * handlers. The order in which conditions are checked is entirely unspecified. Once a
 * condition is satisfied, the associated handler is activated before checking any
 * remaining conditions. Conditions are not checked during the handling of conditions to
 * prevent infinite recursion.
 * <p>
 * Conditions are processed BEFORE all atomic event generating UIContext actions. For
 * example, conditions are checked before the click method in the following code:
 * 
 * <pre>
 *                public void testMain() throws Exception {
 *                    IUIContext ui = getUIContext();
 *                    ui.click(new MenuItemLocator(&quot;New/Project...&quot;));
 *                    ...
 * </pre>
 * 
 * <p>
 * Conditions are added by obtaining this interface from the IUIContext. The
 * {@link com.windowtester.runtime.condition.ICondition#test()} method and the
 * {@link com.windowtester.runtime.condition.IHandler#handle(IUIContext)} method are both
 * called on the WindowTester test thread, so accessing widgets must be through the
 * IUIContext interface, using locator accessor methods such as a implementor of
 * {@link com.windowtester.runtime.condition.IsEnabled#isEnabled(IUIContext)}, using
 * {@link com.windowtester.runtime.swt.condition.SWTUIConditionAdapter}, or using
 * {@link org.eclipse.swt.widgets.Display#syncExec(java.lang.Runnable)}. For example:
 * 
 * <pre>
 *                public void testSomething() throws Exception {
 *                    IUIContext ui = getUIContext();
 *                    IConditionMonitor cm = ui.getAdapter(IConditionMonitor.class);
 *                    cm.addHandler(
 *                        new ICondition() {
 *                            public boolean test() {
 *                                ... test some condition here ...
 *                            }
 *                        },
 *                        new IHandler() {
 *                            public void handle(IUIContext ui) {
 *                                ... take some actions here ...
 *                            }
 *                        }
 *                    );
 *                    ...
 * </pre>
 * 
 * or using {@link com.windowtester.runtime.swt.condition.SWTUIConditionAdapter}
 * 
 * <pre>
 *                public void testSomething() throws Exception {
 *                    IUIContext ui = getUIContext();
 *                    IConditionMonitor cm = ui.getAdapter(IConditionMonitor.class);
 *                    cm.addHandler(
 *                        new SWTUIConditionAdapter() {
 *                            public boolean testUI(Display display) {
 *                                ... test some condition here ...
 *                            }
 *                        },
 *                        new IHandler() {
 *                            public void handle(IUIContext ui) {
 *                                ... take some actions here ...
 *                            }
 *                        }
 *                    );
 *                    ...
 * </pre>
 * 
 * If {@link IConditionMonitor} detects that an {@link ICondition} implements
 * {@link IUICondition}, then {@link IConditionMonitor} calls {@link #testUI(IUIContext)}
 * rather than {@link ICondition#test()}.
 */
public interface IConditionMonitor
{
	/**
	 * A flag returned by {@link #process()} indicating that the conditions were processed
	 * and no conditions were satisified.
	 */
	public static final int PROCESS_NONE = 0;

	/**
	 * A flag returned by {@link #process()} indicating that the conditions were processed
	 * and at least one condition was satisfied.
	 */
	public static final int PROCESS_ONE_OR_MORE = 1;

	/**
	 * A flag returned by {@link #process()} indicating that conditions are already being
	 * processed and that the call returned immediately
	 */
	public static final int PROCESS_RECURSIVE = 2;

	
	/**
	 * A flag returned by {@link #process()} indicating that no conditions were
	 * processed because the application is interacting with native OS functionality 
	 * and that the call returned immediately.  Since conditions might access the UI thread
	 * during processing, it is necessary to skip them when the application goes native
	 * lest the test thread be blocked.
	 */
	public static final int PROCESS_NATIVE = 3;
	
	/**
	 * Add the specified condition and associated handler to the receiver so that it is
	 * included the next time that conditions are processed. WARNING! No checking is
	 * performed to prevent condition/handler pairs from being added multiple times.
	 * 
	 * @param condition the condition to be tested (not <code>null</code>)
	 * @param handler the handler to be activated if the condition is satisified
	 */
	void add(ICondition condition, IHandler handler);

	/**
	 * Add a handler that is called when a dialog matching the specified condition becomes
	 * visible. The handler's <code>test</code> method is called on the UI thread and
	 * the <code>handle</code> is called on the WindowTester test.
	 * 
	 * @param conditionHandler the condition handler to match (not <code>null</code>)
	 */
	void add(IConditionHandler conditionHandler);

	/**
	 * Remove all the registered handlers from this monitor.
	 */
	void removeAll();

	/**
	 * Process all condition/handler pairs by checking each condition and calling the
	 * associated handlers for any conditions that are satisfied. Nested calls to this
	 * method return immediately without taking any action.
	 * 
	 * @param ui the UIContext instance for use in condition handling
	 * @return one of the following flags indicating what was processed:
	 *         {@link #PROCESS_NONE} if conditions were processed but no conditions were
	 *         satisified, {@link #PROCESS_ONE_OR_MORE} if conditions were processed and
	 *         at least on condition was satisified, {@link #PROCESS_RECURSIVE} if
	 *         conditions were already being processed and no additional action was taken.
	 */
	int process(IUIContext ui);
}