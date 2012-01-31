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
package com.windowtester.runtime.swt.internal.operation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.windowtester.runtime.WT;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;

/**
 * Shared behavior for showing a menu and selecting a menu item
 */
public abstract class SWTMenuOperation extends SWTOperation
{
	protected static String priorDebugMsg = "";

	/**
	 * The menu item being
	 */
	protected final MenuItemReference menuItemReference;

	/**
	 * The location at which the mouse should be clicked to show the menu
	 */
	protected SWTLocation location;

	/**
	 * The actual location at which the mouse was clicked to show the menu
	 */
	protected Point clickLoc;

	/**
	 * The menu that appears as a result of the operation. This field is set by
	 * {@link MenuFilter} and should ONLY be accessed from the UI thread.
	 */
	protected Menu menu = null;

	/**
	 * A flag used to indicate the the menu item was selected. This field is set by
	 * {@link MenuFilter} and should ONLY be accessed from the UI thread.
	 */
	protected boolean selected = false;

	/**
	 * If a click occurs in an incorrect location, then this flag is set to indicate that
	 * the selection has been canceled. This field is set by {@link MenuFilter} and should
	 * ONLY be accessed from the UI thread.
	 */
	protected boolean clickCanceled = false;

	/**
	 * If a click occurs in an incorrect location, then this field contains a message
	 * indicating problem. This field is set by {@link MenuFilter} and should ONLY be
	 * accessed from the UI thread.
	 */
	public String clickCanceledMessage = null;

	/**
	 * Set by calling {@link #queueRetryStep(Step)} to cache a step that can be retried if
	 * the menu item location in a dynamic menu shifts after the click occurs. This can
	 * only be called once.
	 */
	protected Step retryStep = null;

	/**
	 * A collection of debugging information appended to any exception thrown
	 */
	protected StringBuilder debugMsg = new StringBuilder(1000);

	/**
	 * Construct a new operation for showing a menu or clicking a menu item
	 * 
	 * @param menuItemReference the menu item to be clicked or <code>null</code> if a top
	 *            level menu is to be clicked.
	 */
	public SWTMenuOperation(MenuItemReference menuItemReference) {
		this.menuItemReference = menuItemReference;
	}

	public SWTMenuOperation waitForEnabled(SWTWidgetReference<?> widgetRef) {
		return (SWTMenuOperation) super.waitForEnabled(widgetRef);
	}

	/**
	 * Queue the mouse events necessary to show the menu or select the menu item.
	 * 
	 * @param accelerator the mouse button such as {@link WT#BUTTON1} or
	 *            {@link WT#BUTTON3} and optionally modifier keys such as {@link WT#SHIFT}
	 * @param location the location for the mouse event
	 * @param pauseOnMouseDown <code>true</code> if there should be a pause between
	 *            posting the mouse down and mouse up event so that the mouse down event
	 *            can be processed before the mouse up event is posted. For TableItem
	 *            context menus on both Windows and Linux, the mouse down events need to
	 *            be processed before the mouse up events are posted. This can be seen in
	 *            the TableDoubleClickTest. For TreeItem context menu on Linux, if right
	 *            mouse button is held down too long, it closes the tree item's context
	 *            menu item.
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTMenuOperation().click(...).execute();</code>
	 */
	public SWTMenuOperation click(final int accelerator, final SWTLocation location, final boolean pauseOnMouseDown) {
		this.location = location;
		queueStartMenuFilter();
		queueRetryStep(new Step() {
			public void executeInUI() throws Exception {
				int button = getButton(accelerator);
				clickLoc = location.location();

				// Debug code to gather more information on menu not visible exceptions
				debugMsg.append("\n   click at " + clickLoc + " accel=" + accelerator + " pause=" + pauseOnMouseDown);
				debugMsg.append("\n   location " + location);
				debugMsg.append("\n   initial active shell " + displayRef.getDisplay().getActiveShell());
				String newDebugMsg = debugMsg.toString();
				debugMsg.setLength(0);
				debugMsg.append("\nPrior show menu operation");
				debugMsg.append(priorDebugMsg);
				debugMsg.append("\nCurrent show menu operation");
				debugMsg.append(newDebugMsg);
				priorDebugMsg = newDebugMsg;

				// Linux needs a wiggle and not just a move
				queueMouseWiggle(clickLoc);

				queueMouseDown(button, clickLoc);

				// Linux needs a mouse move even for a mouse click
				// and it does not hurt on the Windows side
				queueMouseMove(clickLoc);

				if (pauseOnMouseDown)
					queueStep(null);

				queueMouseUp(button, clickLoc);

				// Linux (and Mac?) needs a wiggle to push through events
				queueMouseWiggle(clickLoc);

				queueWaitForMenu();
			}
		});
		return this;
	}

	/**
	 * Answer the menu that appears as a result of executing this operation
	 * 
	 * @return the menu or <code>null</code> if the operation is not complete
	 */
	public MenuReference getMenu() {
		if (menu == null)
			return null;
		return (MenuReference) WTRuntimeManager.asReference(menu);
	}

	//===================================================================================
	// SWT Event Listener

	/**
	 * Queue a step that adds a listener to detect a menu becoming visible
	 */
	protected void queueStartMenuFilter() {
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				menuFilter.start(SWTMenuOperation.this);
			}
		});
	}

	/**
	 * Queue a step that can be retried if the menu item location in a dynamic shifts
	 * after the click occurs. This can only be called once.
	 * 
	 * @param step the step that can be retried
	 */
	protected void queueRetryStep(Step step) {
		if (retryStep != null)
			throw new IllegalStateException("Retry step already set");
		retryStep = step;
		queueStep(step);
	}

	/**
	 * Queue a step that waits until a menu becomes visible
	 */
	protected void queueWaitForMenu() {
		queueStep(new Step() {
			private boolean first = true;

			public void executeInUI() throws Exception {

				// If the click was canceled, then retry if possible

				if (clickCanceled) {
					clickCanceled = false;
					menu = null;
					if (selected) {
						selected = false;
						retryAfterBadSelection(clickCanceledMessage);
					}
					else {
						System.out.println(clickCanceledMessage);
						if (retryStep == null)
							throw new RuntimeException(clickCanceledMessage);
						System.out.println("Retrying last menu item click");
						queueStep(null);
						queueStep(retryStep);
					}
				}
				
				// Check to see if the menu is visible or menu item selected

				else if ((menu == null || !menu.isVisible()) && !selected) {

					// Check for a dynamic menu item that has shifted location

					String message;
					if (clickLoc != null && !clickLoc.equals(location.location())) {
						message = "Menu item moved since click: " + menuItemReference + "\n   clicked at "
							+ clickLoc + "\n   but now location is " + location.location() + debugMsg
							+ "\n   final active shell " + displayRef.getDisplay().getActiveShell();
						if (first) {
							first = false;
							System.out.println(message);
						}
					}
					else
						message = "Menu not visible or selected: " + menuItemReference
							+ debugMsg + "\n   final active shell " + displayRef.getDisplay().getActiveShell();
					
					throw new SWTOperationStepException(message);
				}
			}
		});
	}

	/**
	 * Called after an incorrect selection where the menus are no longer visible.
	 * Subclasses may override.
	 * 
	 * @param message the exception message
	 */
	protected abstract void retryAfterBadSelection(String message);

	protected static final MenuFilter menuFilter = new MenuFilter();

	/**
	 * Watch SWT Events waiting for a menu to become visible.
	 */
	protected static class MenuFilter
		implements Listener
	{
		private SWTMenuOperation operation = null;
		private boolean isListening = false;
		private int openMenuCount = 0;

		void start(SWTMenuOperation operation) {
			this.operation = operation;
			if (!isListening) {
				isListening = true;
				displayRef.getDisplay().addFilter(SWT.Arm, this);
				displayRef.getDisplay().addFilter(SWT.Selection, this);
				displayRef.getDisplay().addFilter(SWT.Show, this);
				displayRef.getDisplay().addFilter(SWT.Hide, this);
				// displayRef.getDisplay().addFilter(SWT.Dispose, this);
			}
		}

		int cancel() {
			if (operation == null)
				return openMenuCount;
			operation = null;
			// If an operation is waiting then a menu might be open that we missed
			return openMenuCount + 1;
		}

		/**
		 * Called by the SWT infrastructure on the UI thread when an SWT event has
		 * occurred.
		 */
		public void handleEvent(Event event) {
			if (event.widget instanceof Menu) {
				switch (event.type) {

					// Detect a menu becoming visible

					case SWT.Show :
						// System.out.println("Menu shown");
						openMenuCount++;
						if (operation != null) {
							operation.menu = (Menu) event.widget;
							operation = null;
						}
						break;

					// If a menu is going away, assume all menus closed

					case SWT.Hide :
						// System.out.println("Menu hidden");
						openMenuCount = 0;
						break;

					// The Dispose event happens out of sequence
					// and sometimes in the middle of a subsequent show/hide
					//		case SWT.Dispose :
					//			System.out.println("Dispose menu");
					//			// If a menu is going away, assume all menus closed
					//			openMenuCount = 0;
					//			break;
				}
			}
			else if (event.widget instanceof MenuItem) {
				switch (event.type) {

					// Detect incorrectly armed menu item and cancel the operation

					case SWT.Arm :
						// System.out.println("Menu item armed");
						if (operation != null) {
							MenuItem actual = (MenuItem) event.widget;
							MenuItem expected = operation.menuItemReference.getWidget();
							if (expected != null && !matches(actual, expected)) {
								String message = "Wrong menu item armed\n   expected: " + expected
									+ "\n   but selected: " + actual;
								System.out.println(message);
								// Cancel a cascaded menu, but let non-cascaded menu continue
								// so that the selection event can be blocked
								if ((actual.getStyle() & SWT.CASCADE) != 0) {
									operation.clickCanceled = true;
									operation.clickCanceledMessage = message;
									operation = null;
								}
							}
						}
						break;

					// Detect incorrectly selected menu item and cancel the operation

					case SWT.Selection :
						// System.out.println("Menu item selected");
						if (operation != null) {
							MenuItem actual = (MenuItem) event.widget;
							MenuItem expected = operation.menuItemReference.getWidget();
							if (expected != null && !matches(actual, expected)) {
								String message = "Wrong menu item selected - canceling selection\n   expected: "
									+ expected + "\n   but selected: " + actual;
								System.out.println(message);
								event.type = SWT.None;
								event.doit = false;
								operation.clickCanceled = true;
								operation.clickCanceledMessage = message;
							}
							operation.selected = true;
							operation = null;
						}
						break;
				}
			}
		}

		/**
		 * Compare two menu items to determine if they are the same
		 */
		private boolean matches(MenuItem actual, MenuItem expected) {
			if (actual == expected)
				return true;
			if (actual == null || expected == null)
				return false;
			String actualText = actual.getText();
			String expectedText = expected.getText();
			if (actualText == expectedText)
				return true;
			if (actualText == null || expectedText == null)
				return false;
			return actualText.equals(expectedText);
		}
	}
}