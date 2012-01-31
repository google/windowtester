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

import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;

/**
 * A operation that waits for a menu to appear. This class provides support for making a
 * menu visible via mouse operations, where as subclasses such as
 * {@link SWTShowViewMenuOperation} provide programmatic menu visibility where it is
 * impractical to open a menu using mouse clicks.
 */
public class SWTShowMenuOperation extends SWTMenuOperation
{
	private int waitForIdleCount = 0;

	/**
	 * Construct a new instance for showing a cascading menu
	 */
	public SWTShowMenuOperation(MenuItemReference menuItemReference) {
		super(menuItemReference);
	}

	/**
	 * Queue a step that waits for a particular menu item to become enabled. Typically,
	 * this is called *before* calling {@link #click(int, SWTLocation, boolean)}
	 * 
	 * @return this operation so that calls can be cascaded on a single line such as
	 * 
	 *         <code>new SWTShowMenuOperation().waitForEnabled(...).openMenu(...).execute();</code>
	 */
	public SWTShowMenuOperation waitForEnabled(SWTWidgetReference<?> widgetRef) {
		return (SWTShowMenuOperation) super.waitForEnabled(widgetRef);
	}

	/**
	 * Queue a step that waits for the display to finish processing events. Typically,
	 * this is called *before* calling {@link #click(int, SWTLocation, boolean)}
	 * 
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTShowMenuOperation().waitForIdle().openMenu(...).execute();</code>
	 */
	public SWTShowMenuOperation waitForIdle() {
		final String className = getClass().getSimpleName();
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				if (!displayRef.isIdle()) {
					if (++waitForIdleCount > 0)
						System.out.println(className + " waiting for idle " + waitForIdleCount);
					throw new SWTOperationStepException("Waiting for idle");
				}
			}
		});
		return this;
	}

	/**
	 * Dismiss any menus that are currently open. This is typically called at the end of
	 * each test to ensure no unintended side-effects carry over into the next test.
	 * 
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTShowMenuOperation().closeAllMenus().execute();</code>
	 */
	public SWTShowMenuOperation closeAllMenus() {
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				int count = menuFilter.cancel();
				if (count == 0)
					return;
				while (count > 0) {
					//TODO: this may be OS-specific...
					queueCharDown(SWT.ESC);
					queueStep(null);
					queueCharUp(SWT.ESC);
					queueStep(null);
					count--;
				}
				// Wait for the UI thread to finish processing the menu closures before continuing
				waitForIdle();
			}
		});
		return this;
	}

	/**
	 * Override superclass implementation to check that {@link #execute()} is being called
	 * from a non-UI thread because menu show requires an intricate dance between UI and
	 * non-UI threads.
	 */
	@Override
	public void execute() {
		if (Thread.currentThread() == displayRef.getDisplay().getThread())
			throw new IllegalStateException("Operations must be called from a non-UI thread");
		super.execute();
	}

	/**
	 * Called after an incorrect selection where the menus are no longer visible.
	 * Subclasses may override.
	 * 
	 * @param message the exception message
	 */
	protected void retryAfterBadSelection(String message) {
		// No possibility of recovery here, so exit and retry in MenuDriver
		System.out.println(message);
		throw new RuntimeException(message);
	}
}