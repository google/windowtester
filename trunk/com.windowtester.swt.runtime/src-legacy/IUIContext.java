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
package com.windowtester.swt;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.swt.condition.ICondition;
import com.windowtester.swt.condition.IConditionMonitor;

/**
 * A UIContext is the main point of entry for interaction with the UI during
 * test playback.
 *<p>
 * <b>UIContext</b> instances are used to perform actions on the User Interface.
 * Actions are primarily performed on <code>Widget<code>s.  For example,
 *</p> 
 * <pre>
 *    IUIContext ui = [a UIContext instance];
 *    Widget widget = [a widget];
 *    ui.click(widget);		
 * </pre>
 *<p>
 * causes the widget <code>widget</code> to be clicked.
 *<p>
 * Widgets can be handled directly (as in this example) or <em>indirectly</em> by
 * way of widget handle identifiers.  Identifiers are <em>registered</em> with the
 * UIContext and use <code>WidgetLocator</code>s to describe widget locations. 
 * For instance, the following snippet associates a button labeled "OK" with the
 * handle "ok.button".
 *</p>
 *<pre>
 *    ui.register("ok.button", new WidgetLocator(Button.class, "OK));
 *</pre> 
 *<p>
 *Having registered this association, we can use the "ok.button" handle as a 
 *way to refer to the OK button in the UI. That is, clicking "OK" could be 
 *performed as follows:
 *
 * <pre>
 *		ui.click("ok.button");
 * </pre>
 * 
 *<p>
 *<p>
 *@see com.windowtester.swt.WidgetLocator 
 * @deprecated Use {@link com.windowtester.runtime.IUIContext} instead
 */
 public interface IUIContext {

	
	////////////////////////////////////////////////////////////////////////
	//
	// Widget handle registration
	//
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * Registers this handle, locator pair.
	 * @param handle - the handle to register.
	 * @param locator - the associated locator object.
	 */
	 void register(String handle, WidgetLocator locator);
	
    ///////////////////////////////////////////////////////////////////////////
	//
	// Adapter interface
    //
	///////////////////////////////////////////////////////////////////////////
		 
	/**
	 * Returns an object which is an instance of the given class associated
	 * with this object. Returns <code>null</code> if no such object can
	 * be found.
	 * 
	 * @param adapter
	 *            the adapter class to look up
     * @return an object castable to the given class, or <code>null</code>
	 *         if this object does not have an adapter for the given class
	 */
	Object getAdapter(Class adapter); 
	 
	 
	///////////////////////////////////////////////////////////////////////////
	//
	// Click actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	/** Click in the center of the given component.
	 * @return the clicked widget
	 */
	Widget click(Widget widget);

	/** Click in the given part of the component.
	 * @return the clicked widget
	 */
	Widget click(Widget w, int x, int y);
	
	/** Click in the center of the given component, identified by handle.
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget click(String handle) throws WidgetNotFoundException, MultipleWidgetsFoundException;
		
	/** Click in the given part of the componentt, identified by handle.
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget click(String handle, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/** Click in the center of the given component.
	 * @param buttonMask the button mask to use in the click (e.g., SWT.BUTTON1)
	 * @return the clicked widget
	 */
	Widget click(Widget widget, int buttonMask);
	
	/** Click in the given part of the component.
	 * @return the clicked widget
	 */
	Widget click(Widget w, int x, int y, int mask);

	/** Click in the center of the given component, identified by handle.
	 * @param buttonMask the button mask to use in the click (e.g., SWT.BUTTON1)
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see IUIContext#click(Widget, int)
	 */
	Widget click(String ownerHandle, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/** Click the widget described by this path or label relative to 
	 * its "owner" widget.
	 * <br><br>
	 * Example uses include:
	 * <br>
	 * <pre>
	 *     click(listWidget, "List Item A");
	 *     click(treeWidget, "path/to/treeNode");
	 *     click(menuWidget, "path/to/menuItem");
	 *     click(tableItem,  "3"); //click item, column 3 (0-based)
	 * </pre>
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget click(Widget owner, String labelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/** Click the widget described by this path or label relative to 
	 * its "owner" widget.	 
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see IUIContext#click(Widget, String)
	 */
	Widget click(String ownerHandle, String labelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/** Click the widget described by this path or label relative to 
	 * its "owner" widget.
	 * <br><br>
	 * Example uses include:
	 * <br>
	 * <pre>
	 *     click(listWidget, "List Item A", SWT.BUTTON1);
	 *     click(treeWidget, "path/to/treeNode", SWT.BUTTON1 | SWT.CHECK);
	 *     click(menuWidget, "path/to/menuItem", SWT.BUTTON3);
	 *     
	 * </pre>
	 * @return the clicked widget
	 */
	Widget click(Widget widget, String labelOrPath, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/** Click the widget described by this path or label relative to 
	 * its "owner" widget.	 
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see IUIContext#click(Widget, String, int)
	 */
	Widget click(String ownerHandle, String labelOrPath, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/** Double-click in the center of the given component.
	 * @return the clicked widget
	 */
	Widget doubleClick(Widget widget);

	/** Double-click in the given part of the component.
	 * @return the clicked widget
	 */
	Widget doubleClick(Widget w, int x, int y);
	
	/** Double-click the widget described by this path or label relative to 
	 * its "owner" widget.
	 * <br><br>
	 * Example uses include:
	 * <br>
	 * <pre>
	 *     click(listWidget, "List Item A");
	 *     click(treeWidget, "path/to/treeNode");
	 *     click(menuWidget, "path/to/menuItem");
	 *     click(tableItem,  "3"); //click item, column 3 (0-based)
	 * </pre>
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget doubleClick(Widget owner, String labelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	/** Double-click in the center of the given component.
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget doubleClick(String handle) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	/** Double-click in the given part of the component.
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget doubleClick(String handle, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	/** Double-click in the center of the given component.
	 * @param buttonMask the button mask to use in the click (e.g., SWT.BUTTON1)
	 * @return the clicked widget
	 */
	Widget doubleClick(Widget widget, int buttonMask);
	

	/** Double-click in the given part of the component.
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget doubleClick(Widget widget, int x, int y, int buttonMask);
	
	
	/** Double-click in the center of the given component.
	 * @param buttonMask the button mask to use in the click (e.g., SWT.BUTTON1)
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget doubleClick(String widgetHandle, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	/** Double-click in the given part of the component.
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget doubleClick(String widgetHandle, int x, int y, int buttonMask) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	/** Double-click the widget described by this path or label relative to 
	 * its "owner" widget.	 
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget doubleClick(String ownerHandle, String labelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException;

	
	/** Context click the given widget and select the menu item described by this
	 * path.
	 * <br><br>
	 * For example:
	 * <br>
	 * <pre>
	 *     contextClick(widget, "path/to/menuItem");
	 * </pre>
	 * @return the clicked widget
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 */
	Widget contextClick(Widget widget, String path) throws MultipleWidgetsFoundException, WidgetNotFoundException;
	
	/** Context click the given widget (at the relative x, y offset) and select the menu item described by this
	 * path.
	 * <br><br>
	 * For example:
	 * <br>
	 * <pre>
	 *     contextClick(widget, 5, 10, "path/to/menuItem");
	 * </pre>
	 * @return the clicked widget
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 */
	Widget contextClick(Widget widget, int x, int y, String path) throws MultipleWidgetsFoundException, WidgetNotFoundException;
	
		
	/** Context click the given widget and select the menu item described by this
	 * path.
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget contextClick(String widgetHandle, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	/** Context click the given widget (at the relative x, y offset) and select the menu item described by this
	 * path.
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	Widget contextClick(String widgetHandle, int x, int y, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	
	/** Context click the given widget (identified by path) and select the menu item described by this
	 * path.
	 * <br><br>
	 * For example:
	 * <br>
	 * <pre>
	 *     contextClick(widget, "path/to/item", "path/to/menuItem");
	 * </pre>
	 * @return the clicked widget
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 */
	Widget contextClick(Widget widget, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException;

	
	
	/** Context click the given widget (identified by path) and select the menu item described by this
	 * path.
	 * <br><br>
	 * For example:
	 * <br>
	 * <pre>
	 *     contextClick(widgetHandle, "path/to/item", "path/to/menuItem");
	 * </pre>
	 * @return the clicked widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 */
	Widget contextClick(String widgetHandle, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException;

	
	
/**
 * Defered until later release; also: consider renaming to clickToggle(..)
 */
	
//	/** Click to expand this widget.  In the case of a Tree Item, 
//	 * it expands the tree node.  In the case of a Tool Item, it expands
//	 * the pull down menu.
//	 * @return the clicked widget
//	 */
//	Widget clickExpand(Widget widget);
//	
//	
//	/** Click to expand this widget.  In the case of a Tree Item, 
//	 * it expands the tree node.  In the case of a Tool Item, it expands
//	 * the pull down menu.
//	 * @return the clicked widget
//	 * @throws MultipleWidgetsFoundException 
//	 * @throws WidgetNotFoundException 
//	 */
//	Widget clickExpand(String widgetHandle) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Drag and drop actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Perform a drag operation (starting at the current hover position of the mouse) 
	 * to the center of the given target widget and drop. 
	 * @param target the target of the drag
	 * @return the target widget
	 * @see IUIContext#dragTo(int, int)
	 */
	Widget dragTo(Widget target);
	
	/**
	 * Perform a drag operation (starting at the current hover position of the mouse)
	 * to the offset relative to the given widget and drop. 
	 * @param target the target of the drag
	 * @param x the x offset
	 * @param y the y offset
	 * @return the target widget
	 * @see IUIContext#dragTo(int, int)
	 */
	Widget dragTo(Widget target, int x, int y);
		
	/**
	 * Perform a drag operation (starting at the current hover position of the mouse) 
	 * to the item in this widget identified by path.
	 * @param w the parent widget (e.g., Tree)
	 * @param path the path string (e.g., "parent/child")
	 * @param x the x offset
	 * @param y the y offset
	 * @return the target widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see IUIContext#dragTo(int, int)
	 */
	Widget dragTo(Widget w, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/**
	 * Perform a drag operation (starting at the current hover position of the mouse) 
	 * to the center of the given target widget identified by handle and drop. 
	 * @param widgetHandle the handle of the target of the drag
	 * @return the target widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see IUIContext#dragTo(int, int)
	 */
	Widget dragTo(String widgetHandle) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	/**
	 * Perform a drag operation (starting at the current hover position of the mouse)
	 * to the offset relative to the given widget identfied by handle and drop. 
	 * @param widgetHandle the handle of the target of the drag
	 * @param x the x offset
	 * @param y the y offset
	 * @return the target widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see IUIContext#dragTo(int, int)
	 */
	Widget dragTo(String widgetHandle, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	/**
	 * Perform a drag operation (starting at the current hover position of the mouse) 
	 * to the item in this widget identified by path.
	 * @param widgetHandle the handle of the target of the drag
	 * @param path the path string (e.g., "parent/child")
	 * @param x the x offset
	 * @param y the y offset
	 * @return the target widget
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see IUIContext#dragTo(int, int)
	 */
	Widget dragTo(String widgetHandle, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
	
	
	/**
	 * Perform a drag to this absolute (display-relative) x,y coordinate.
	 * <p>
	 * Drag and drop operations use an <em>implicit</em> drag source: the drag source is
	 * taken to be the last hover position of the mouse before <code>dragTo(..)</code> 
	 * is called.  Typically the drag source is identified using a <code>mouseMove(..)</code>
	 * operation:
	 * <pre>
	 *    Widget textFile = ...;
	 *    Widget editor = ...;
	 *    ui.mouseMove(textFile, 5, 5);
	 *    ui.dragTo(editor, 100, 100);
	 * </pre>
	 * Alternatively, a drag source can be specified using a <code>click</code>
	 * operation:
	 * <pre>
	 *    Widget projTree = ...;
	 *    ui.click(projTree, "TestProject/file.txt");
	 *    ui.dragTo(projectTree, "TestProject/folder", 5, 5);
	 * </pre>
	 * 
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	void dragTo(int x, int y);
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Primitive mouse action commands
	//
	///////////////////////////////////////////////////////////////////////////
		
	/**
	 * Move the mouse to hover over the center of this widget
	 * @param w - the widget to hover over
	 */
	void mouseMove(Widget w);

	/**
	 * Move the mouse to hover over this widget using the given x,y offsets.
	 * @param w - the widget to hover over
	 */
	void mouseMove(Widget w, int x, int y);

	/**
	 * Move the mouse to this x,y coordinate.
	 * @param x the target x coordinate
	 * @param y the target y coordinate
	 */
	void mouseMove(int x, int y);
	
	/**
	 * Press the mouse.
	 * @param accel - the mouse accelerator.
	 */
	void mouseDown(int accel);
	
	/**
	 * Release the mouse
	 * @param accel - the mouse accelerator.
	 */
	public void mouseUp(int accel);

	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Text entry actions
	//
	///////////////////////////////////////////////////////////////////////////
		
	/** Enter the given text.
	 */
	void enterText(String txt);

	/** Click the given key.
	 */
	void keyClick(int key);

	/** Click the given key.
	 */
	void keyClick(char key);

	/** Click the given keystroke.
	 */
	public void keyClick(int ctrl, char c);
	
	/** Depress the given key.
	 */
	void keyDown(char key);

	/** Release the given key.
	 */
	void keyUp(char key);

	/** Depress the given key.
	 */
	void keyDown(int key);

	/** Release the given key.
	 */
	void keyUp(int key);
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Selection actions
	//
	///////////////////////////////////////////////////////////////////////////
	
//	/** 
//	 * Select this range of items in the given widget. 
//	 * @param w - the widget in which to select
//	 * @param start - the starting index
//	 * @param stop - the stop index
//	 */
//	void select(Widget w, int start, int stop);
//	
//	/** 
//	 * Select this range of items in the given widget. 
//	 * @param handle - the handle for the widget in which to select
//	 * @param start - the starting index
//	 * @param stop - the stop index
//	 * @throws MultipleWidgetsFoundException 
//	 * @throws WidgetNotFoundException 
//	 */
//	void select(String handle, int start, int stop) throws WidgetNotFoundException, MultipleWidgetsFoundException;
//		
//		
//	/** 
//	 * Select all the items in the given widget. 
//	 * @param handle - the handle for the widget in which to select
//	 * @throws MultipleWidgetsFoundException 
//	 * @throws WidgetNotFoundException 
//	 */
//	void selectAll(String handle) throws WidgetNotFoundException, MultipleWidgetsFoundException;
//	
//	/** 
//	 * Select all the items in the given widget. 
//	 * @param w - the widget in which to select
//	 */
//	void selectAll(Widget w);
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Meta actions
	//
	///////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Requests that the window manager close the receiver shell in
	 * the same way it would be closed when the user clicks on
	 * the "close box" or performs some other platform specific
	 * key or mouse combination that indicates the window
	 * should be removed.
	 * @param shell - the shell to close
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see Shell#close()
	 */
	void close(Shell shell);	
	
	/** Close the shell identified by this handle
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	void close(String shellHandle) throws WidgetNotFoundException, MultipleWidgetsFoundException;	
	
	/**
	 * Ensure that the control containing this widget has keyboard focus.
	 * @param widget - the widget whose control should have focus
	 */
	void setFocus(Widget widget);
	
	/**
	 * Ensure that the control containing this widget has keyboard focus.
	 * @param widgetHandle - the handle for widget whose control should have focus
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	void setFocus(String widgetHandle) throws WidgetNotFoundException, MultipleWidgetsFoundException;
	
//
//	To be revisited for a later release.
//  NOTE: we also want to do maximize and minimize; moreover, we should probably move to
//  a single setBounds method --- resize does not handle resizing from the top left corner!
//
	
//	/** Move keyboard focus to the given component. 
//	 */
//	void focus(Control control);
//		
//	/** Move this control.
//	 * @param control - the control to move
//	 * @param x - the new x coordinate
//	 * @param y - the new y coordinate
//	 */
//	void move(Control control, int x, int y);
//	
//	/** Move this control (by handle).
//	 * @param controlHandle - the control to move
//	 * @param x - the new x coordinate
//	 * @param y - the new y coordinate
//	 * @throws MultipleWidgetsFoundException 
//	 * @throws WidgetNotFoundException 
//	 */
//	void move(String controlHandle, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException;
//	
//	/**
//	 * Resize this control.
//	 * @param control - the control to resize
//	 * @param width - the new width
//	 * @param heigth - the new height
//	 */
//	void resize(Control control, int width, int height);
//	
//	/**
//	 * Resize this control (by handle).
//	 * @param controlHandle - the control to resize
//	 * @param width - the new width
//	 * @param heigth - the new height
//	 * @throws WidgetNotFoundException 
//	 */
//	void resize(String controlHandle, int width, int height) throws WidgetNotFoundException, MultipleWidgetsFoundException;

		
	////////////////////////////////////////////////////////////////////////
	//
	// Condition-handling
	//
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * Check for any active conditions and handle them. If a condition is handled,
	 * original hover context will be restored post condition handling.
	 * @return one of the following flags indicating what was processed:
	 * {@link IConditionMonitor#PROCESS_NONE} if conditions were processed but no conditions were satisified,
	 * {@link IConditionMonitor#PROCESS_ONE_OR_MORE} if conditions were processed and at least on condition was satisified,
	 * {@link IConditionMonitor#PROCESS_RECURSIVE} if conditions were already being processed and no additional action was taken.
	 */
	public int handleConditions();
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Timing
	//
	///////////////////////////////////////////////////////////////////////////
	
	/** Wait for the display to be idle.
	 */
	void waitForIdle();

	/** Wait for a shell to be displayed using the default timeout (usually 5000 ms).
	 * @see IUIContext#waitForShellShowing(String, int)
	 */
	void waitForShellShowing(String shellName);
	
	/** Wait for a shell to be displayed.  
	 * @param timeout in millis
	 */
	void waitForShellShowing(String shellName, int timeout);

	/** Wait for a shell to be disposed using the default timeout (usually 5000 ms).
	 * @see IUIContext#waitForShellShowing(String, int)
	 */
	void waitForShellDisposed(String shellName);
	
	/** Wait for a shell to be disposed.  
	 * @param timeout in millis
	 */
	void waitForShellDisposed(String shellName, int timeout);
	
	
	/** Pause for a given number of milliseconds.
	 */
	void pause(int ms);
	
	/**
	 * Wait for the given condition to evaluate to true.
	 * @param condition the condition to wait for
	 * @throws WaitTimedOutException if the default timeout (30s) is exceeded. 
	 */
	void wait(ICondition condition);
	
	/** Wait for the given Condition to return true, waiting for timeout ms.
	 * @throws WaitTimedOutException if the timeout is exceeded. 
	 */
	void wait(ICondition condition, long timeout);
	
	/** Wait for the given Condition to return true, waiting for timeout ms,
	 * polling at the given interval.
	 * @throws WaitTimedOutException if the timeout is exceeded. 
	 */
	void wait(ICondition condition, long timeout, int interval);
	
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Widget finding convenience
	//
	///////////////////////////////////////////////////////////////////////////

	/** Find the widget associated with this handle.
	 */
	Widget find(String handle) throws WidgetNotFoundException, MultipleWidgetsFoundException;

	/**
	 * @return the associated Display instance
     */
	Display getDisplay();

	/** Find the widget identified by this widget locator.
	 * @param locator the widget locator
	 * @return the identified widget
	 * @throws WidgetNotFoundException
	 * @throws MultipleWidgetsFoundException 
	 */
	Widget find(WidgetLocator locator) throws WidgetNotFoundException, MultipleWidgetsFoundException;






}
