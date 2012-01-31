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
package com.windowtester.runtime;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.condition.IConditionMonitor;
import com.windowtester.runtime.condition.IHandler;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IMenuItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;

/**
 * The main point of entry for interaction with the UI during test
 * playback.
 * <p>
 * <b>IUIContext</b> instances are used to perform actions on the User Interface.
 * Actions are primarily performed on <code>Widget<code>s.  For example,
 * </p> 
 * <pre>
 *		IUIContext ui = getUIContext();
 *		IWidgetLocator widget = [a widget];
 *		ui.click(widget);		
 * </pre>
 * <p>
 * causes the widget <code>widget</code> to be clicked.
 * <p>
 * More commonly, widgets are handled <em>indirectly</em> by way of widget locators. 
 * For instance, the following snippet clicks a button labeled "OK".
 * </p>
 * <pre>
 *		IUIContext ui = getUIContext();
 *		ui.click(new ButtonLocator(&quot;OK&quot;));
 * </pre> 
 * <p>
 * @see ILocator 
 */
public interface IUIContext
{
		
	// /////////////////////////////////////////////////////////////////////////
	//
	// Adapter interface
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an object which is an instance of the given class associated with this
	 * object. Returns <code>null</code> if no such object can be found.
	 * 
	 * @param adapter the adapter class to look up
	 * @return an object castable to the given class, or <code>null</code> if this
	 *         object does not have an adapter for the given class
	 */
	Object getAdapter(Class<?> adapter);

	// /////////////////////////////////////////////////////////////////////////
	//
	// Click actions
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Click the given component, identified by locator.
	 * For example:
	 * <pre>
	 * 		ui.click(new MenuLocator("File/New/Project..."));
	 * 		ui.click(new TreeItemLocator(new WidgetLocator(new ViewLocator("Navigator"), Tree.class), "MyProject/src/package1")));
	 * 		ui.click(new XYLocator(new WidgetLocator(Canvas.class), 2, -2));
	 * 		ui.click(new WidgetLocator(Button.class, "OK"));
	 * </pre>
	 * This is fully equivalent to
	 * <pre>
	 *		ui.click(1, locator, WT.NONE);
	 * </pre>
	 * 
	 * @param locator the locator identifying where the click should occur
	 * @return the clicked widget
	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
	 */
	IWidgetLocator click(ILocator locator) throws WidgetSearchException;

	/**
	 * Click the given component, identified by locator.
	 * This is fully equivalent to
	 * <pre>
	 *		ui.click(clickCount, locator, WT.NONE);
	 * </pre> 
	 * See {@link #click(ILocator)} for examples 
	 * 
	 * @param clickCount the number of times the widget should be clicked
	 * @param locator the locator identifying where the click should occur
	 * @return the clicked widget
	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
	 */
	IWidgetLocator click(int clickCount, ILocator locator) throws WidgetSearchException;

	/**
	 * Click the given component, identified by locator.
	 * See {@link #click(ILocator)} for examples 
	 * 
	 * @param clickCount the number of times the widget should be clicked
	 * @param locator the locator identifying where the click should occur
	 * @param modifierMask the modifier mask to use in the click (e.g. {@link WT#SHIFT})
	 * @return the clicked widget
	 */
	IWidgetLocator click(int clickCount, ILocator locator, int modifierMask) throws WidgetSearchException;

	/**
	 * Context click the given widget and select the menu item described by this path.
	 * For example:
	 * <pre>
	 * 		ui.contextClick(
	 * 			TreeItemLocator("MyProject/src/package1", new ViewLocator("Navigator")),
	 * 			new MenuLocator("New/File..."));
	 * </pre>
	 * This is fully equivalent to
	 * <pre>
	 *		ui.contextClick(locator, path, WT.NONE);
	 * </pre> 
	 * 
	 * @param locator the locator identifying where the click should occur
	 * @param menuItem the locator identifying the menu item to be selected
	 * @return the widget identified by the locator argument
	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
	 */
	IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem) throws WidgetSearchException;

	/**
	 * Context click the given widget and select the menu item described by this path.
	 * See {@link #contextClick(ILocator, IMenuItemLocator)} for examples.
	 * 
	 * @param locator the locator identifying where the click should occur
	 * @param menuItem the locator identifying the menu item to be selected
	 * @param modifierMask the modifier mask to use in the click (e.g. {@link WT#SHIFT})
	 * @return the widget identified by the locator argument
	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
	 */
	IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem, int modifierMask) throws WidgetSearchException;

	/**
	 * Context click the given widget and select the menu item described by this path.
	 * For example:
	 * <pre>
	 * 		ui.contextClick(
	 * 			new TreeItemLocator("MyProject/src/package1", new ViewLocator("Navigator")),
	 * 			"New/File");
	 * </pre>
	 * 
	 * @param locator the locator identifying where the click should occur
	 * @param menuItem the locator identifying the menu item to be selected
	 * @return the widget identified by the locator argument
	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
	 */
	IWidgetLocator contextClick(ILocator locator, String menuItem) throws WidgetSearchException;

	/**
	 * Context click the given widget and select the menu item described by this String path.
	 * 
	 * @param locator the locator identifying where the click should occur
	 * @param menuItem the locator identifying the menu item to be selected
	 * @param modifierMask the modifier mask to use in the click (e.g. {@link WT#SHIFT})
	 * @return the widget identified by the locator argument
	 */
	IWidgetLocator contextClick(ILocator locator, String menuItem, int modifierMask) throws WidgetSearchException;

		
	
	
	// /////////////////////////////////////////////////////////////////////////
	//
	// Drag and drop actions
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Move the mouse to hover over the specified location
	 * For example:
	 * <pre>
	 * 		ui.mouseMove(new TreeItemLocator(new WidgetLocator(new ViewLocator("Navigator"), Tree.class), "MyProject/src/package1")));
	 * 		ui.mouseMove(new XYLocator(new WidgetLocator(Canvas.class), 2, -2, WT.LEFT | WT.BOTTOM));
	 * 		ui.mouseMove(new WidgetLocator(Button.class, "OK"));
	 * </pre>
	 * 
	 * @param locator the locator identifying where the spot over which the mouse should hover
	 * @return the widget over which the mouse is hovering
	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
	 */
	IWidgetLocator mouseMove(ILocator locator) throws WidgetSearchException;

	/**
	 * Perform a drag operation (starting at the current hover position of the mouse) to
	 * the specified location and drop.
	 * For example:
	 * <pre>
	 * 		ui.dragTo(new TreeItemLocator(new WidgetLocator(new ViewLocator("Navigator"), Tree.class), "MyProject/src/package1")));
	 * 		ui.dragTo(new XYLocator(new WidgetLocator(Canvas.class), 2, -2, WT.LEFT | WT.BOTTOM));
	 * </pre>
	 * This is fully equivalent to
	 * <pre>
	 *		ui.dragTo(locator, WT.NONE);
	 * </pre> 
	 * 
	 * @param locator the locator identifying where the drop should occur
	 * @return the widget where the drop occurred
	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
	 */
	IWidgetLocator dragTo(ILocator locator) throws WidgetSearchException;

	/**
	 * Perform a drag operation (starting at the current hover position of the mouse) to
	 * the specified location and drop.
	 * See {@link #dragTo(ILocator)} for examples.
	 * 
	 * @param locator the locator identifying where the drop should occur
	 * @param modifierMask the modifier mask to use during the drag and drop
	 * 			operation (e.g. {@link WT#SHIFT})
	 * @return the widget where the drop occurred
	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
	 */
	IWidgetLocator dragTo(ILocator locator, int modifierMask) throws WidgetSearchException;

	// /////////////////////////////////////////////////////////////////////////
	//
	// Text entry actions
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Enter the given text.
	 */
	void enterText(String txt);

	/**
	 * Click the given key (by constant).
	 * <p>
	 * Composite key clicks can be specified by bitwise or-ing  key constants.  
	 * For example:
	 * 
	 * <pre> ui.keyClick(WT.CTRL | WT.HOME) </pre>
	 * 
	 * specifies a combined key click of the <em>Control</em> and <em>Home</em> keys.
	 */
	void keyClick(int key);

	/**
	 * Click the given key.
	 */
	void keyClick(char key);

	/**
	 * Click the given keystroke.
	 * 
	 * @param modifierMask the modifier mask to use in the click (e.g. {@link WT#SHIFT})
	 */
	public void keyClick(int modifierMask, char c);

	// /////////////////////////////////////////////////////////////////////////
	//
	// Meta actions
	//
	// /////////////////////////////////////////////////////////////////////////


	/**
	 * Close the specified window in the same way it would be closed when the user 
	 * clicks on the "close box" or performs some other platform specific key 
	 * or mouse combination that indicates the window should be closed.
	 * For example:
	 * <pre>
	 * 		ui.close(new WidgetLocator(Shell.class, "My SWT Window"));
	 * 		ui.close(new WidgetLocator(JFrame.class, "My Swing Window"));
	 * </pre>
	 * 
	 * @param locator the locator identifying window that should be closed.
	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
	 * @deprecated prefer {@link #ensureThat(IConditionHandler)} instead
	 */
	void close(IWidgetLocator locator) throws WidgetSearchException;

//	/**
//	 * Ensure that the specified widget has keyboard focus.
//	 * For example:
//	 * <pre>
//	 * 		ui.setFocus(new LabeledWidgetLocator(Text.class, "Name:"));
//	 * </pre>
//	 * 
//	 * @param locator the locator identifying widget to have focus
//	 * @return the widget that received focus
//	 * @throws WidgetSearchException if the locator is a widget locator and the widget cannot be found
//	 */
//	IWidgetLocator setFocus(IWidgetLocator locator) throws WidgetSearchException;

	// //////////////////////////////////////////////////////////////////////
	//
	// Condition-handling
	//
	// //////////////////////////////////////////////////////////////////////
	
	/**
	 * Assert that the given condition either is true or becomes true in a reasonable amount of time.
	 * Some examples include:
	 * <pre>
	 * 		ui.assertThat(new IsEnabledCondition(new ButtonLocator("OK"), false));
	 * 		ui.assertThat(new HasTextCondition(new WizardErrorMessageLocator(), "File extension is missing"));
	 * </pre>
	 * 
	 * @param condition the condition to assert
	 * @throws WaitTimedOutException if the default timeout (3 seconds) is exceeded.
	 */
	void assertThat(ICondition condition) throws WaitTimedOutException;
	
	/**
	 * Assert that the given condition either is true or becomes true in a reasonable amount of time.
	 * Some examples include:
	 * <pre>
	 * 		ui.assertThat(new IsEnabledCondition(new ButtonLocator("OK"), false));
	 * 		ui.assertThat(new HasTextCondition(new WizardErrorMessageLocator(), "File extension is missing"));
	 * </pre>
	 * 
	 * @param message the message displayed if the condition is not and does not become <code>true</code>
	 * @param condition the condition to assert (not null)
	 * @throws WaitTimedOutException if the default timeout (3 seconds) is exceeded.
	 */
	void assertThat(String message, ICondition condition) throws WaitTimedOutException;

	/**
	 * Ensure that the given condition either is true or becomes true in a reasonable amount of time.  If the condition 
	 * is not true within the default timeout (of 3 seconds), the condition's associated handler
	 * ({@link IConditionHandler#handle(IUIContext)} is called.
	 * <p/>
	 * For example, suppose we have a condition/handler defined like this:
	 * <pre>
	 *   class IntroPageUnzoomedCondition implements IConditionHandler {
	 *      public boolean testUI(IUIContext ui) {
	 *         ... test zoom state ...
	 *      }
	 *      public void handle(IUIContext ui) throws Exception {
	 *         ... unzoom intro ...
	 *      }
	 *   }
	 * </pre>
	 * 
	 * then a call like this:
	 * 
	 * <pre>
	 *   ui.ensureThat(new IntroPageUnzoomedCondition());
	 * </pre>
	 * 
	 * tests to see if the Welcome page is dismissed.  If it is, the call returns immediately.  If not, a {@link #wait(ICondition)}
	 * is performed with the same timeout as assertions ({@link #assertThat(ICondition)}.  If it is not dismissed in that interval,
	 * then {@link IConditionHandler#handle(IUIContext)} is called.  Finally {@link #assertThat(ICondition)} is called to ensure
	 * the handler succeeded.
	 * 
	 * @param conditionHandler the condition to test and handle
	 * @throws WaitTimedOutException if the default timeout (3 seconds) is exceeded.
	 * @throws Exception if an exception occurs during the call to {@link IHandler#handle(IUIContext)}
	 * 
	 * @since 3.7.1
	 */
	void ensureThat(IConditionHandler conditionHandler) throws WaitTimedOutException, Exception;
	
	
	/**
	 * Wait for the given condition to evaluate to true. Some examples include:
	 * 
	 * <pre>
	 * ui.wait(new IdleCondition());
	 * ui.wait(new ShellShowingCondition(&quot;My SWT Window Title&quot;));
	 * ui.wait(new WindowDisposed(JFrame.class, &quot;My Swing Window Title&quot;));
	 * </pre>
	 * 
	 * This is fully equivalent to
	 * 
	 * <pre>
	 * ui.wait(condition, WT.DEFAULT_TIMEOUT, WT.DEFAULT_INTERVAL);
	 * </pre>
	 * 
	 * If this context detects that an {@link ICondition} implements {@link IUICondition},
	 * then {@link IConditionMonitor} calls {@link #testUI(IUIContext)} rather than
	 * {@link ICondition#test()}.
	 * 
	 * @param condition the condition to wait for
	 * @throws WaitTimedOutException if the default timeout (30 seconds) is exceeded.
	 */
	void wait(ICondition condition) throws WaitTimedOutException;

	/**
	 * Wait for the given Condition to return true, waiting for timeout ms. This is fully
	 * equivalent to
	 * 
	 * <pre>
	 * ui.wait(condition, timeout, WT.DEFAULT_INTERVAL);
	 * </pre>
	 * 
	 * See {@link #wait(ICondition)} for examples.
	 * <p>
	 * If this context detects that an {@link ICondition} implements {@link IUICondition},
	 * then {@link IConditionMonitor} calls {@link #testUI(IUIContext)} rather than
	 * {@link ICondition#test()}.
	 * 
	 * @param condition the condition to wait for
	 * @param timeout the number of milliseconds to wait for the condition to become true
	 * @throws WaitTimedOutException if the timeout is exceeded.
	 */
	void wait(ICondition condition, long timeout) throws WaitTimedOutException;

	/**
	 * Wait for the given Condition to return true, waiting for timeout ms, polling at the
	 * given interval. See {@link #wait(ICondition)} for examples.
	 * <p>
	 * If this context detects that an {@link ICondition} implements {@link IUICondition},
	 * then {@link IConditionMonitor} calls {@link #testUI(IUIContext)} rather than
	 * {@link ICondition#test()}.
	 * 
	 * @param condition the condition to wait for
	 * @param timeout the number of milliseconds to wait for the condition to become true
	 * @param interval the number of milliseconds to sleep between condition checks
	 * @throws WaitTimedOutException if the timeout is exceeded.
	 */
	void wait(ICondition condition, long timeout, int interval) throws WaitTimedOutException;

	/**
	 * Check for any active conditions and handle them. If a condition is handled,
	 * original hover context will be restored post condition handling.
	 * 
	 * @return one of the following flags indicating what was processed:
	 *         {@link IConditionMonitor#PROCESS_NONE} if conditions were processed but no conditions were satisfied, 
	 *         {@link IConditionMonitor#PROCESS_ONE_OR_MORE} if conditions were processed and at least on condition was satisfied,
	 *         {@link IConditionMonitor#PROCESS_RECURSIVE} if conditions were already being processed and no additional action was taken.
	 */
	public int handleConditions();
	
	
	/**
	 * Get this UI's {@link IConditionMonitor}.
	 * 
	 * @return the UI's Condition Monitor
	 * @since 3.6.2
	 */
	public IConditionMonitor getConditionMonitor();

	// /////////////////////////////////////////////////////////////////////////
	//
	// Timing
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Pause for a given number of milliseconds.
	 * <p>
	 * As a general rule pauses are discouraged in UI tests.  Instead waiting for an
	 * appropriate {@link ICondition} is preferred.  For example, if a pause has been
	 * inserted in order for the test to sync up with an application that is performing a
	 * long-running operation, a condition that tests if the operation is complete should 
	 * be used instead.  In eclipse, long operations are often wrapped in platform
	 * <code>Job</code>s so the following wait is a good substitute for a pause:
	 * <p>
	 * <code>
	 *    ui.wait(new JobsCompleteCondition());
	 * </code>
	 * </p>
	 * In the rare case where a pause for a set period of time really is desirable,
	 * a {@link TimeElapsedCondition} should be used instead.
	 * 
	 * </p>
	 * @param ms the number of milliseconds to wait
	 *
	 * @deprecated Use {@link #wait(ICondition)} instead.
	 */
	void pause(int ms);

	// /////////////////////////////////////////////////////////////////////////
	//
	// Widget finding convenience
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Find the specified widget.  
	 * <p>
	 * If the widget locator is known to
	 * resolve to a basic widget (such as a tree, button, tab, etc.), it is
	 * safe to cast the returned locator to an instance of {@link WidgetReference}.
	 * From this reference, the target widget can be obtained:
	 * <pre>
	 * WidgetReference ref = (WidgetReference)ui.find(myLocator);
	 * Widget foundWidget  = (Widget)ref.getWidget();
	 * </pre>
	 * 
	 * 
	 * @param locator the locator specifying the widget to be found.
	 */
	IWidgetLocator find(IWidgetLocator locator) throws WidgetSearchException;

	/**
	 * Find all widgets matching the specified criteria
	 * 
	 * @param locator the locator specifying the widget to be found.
	 * 		This can be an IWidgetLocator indicating which widget
	 * 		or an IXYLocator specifying a point within the widget's bounds.
	 */
	IWidgetLocator[] findAll(IWidgetLocator locator);
	
	/**
	 * Answer the current window being manipulated during playback.
	 * 
	 * @return the window (e.g. Shell, JFrame, ...) or <code>null</code> if none.
	 */
	Object getActiveWindow();
}
