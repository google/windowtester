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
import org.eclipse.swt.widgets.MenuItem;

import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;

/**
 * Perform operations on {@link MenuItem}s.
 */
public class SWTMenuItemOperation extends SWTMenuOperation
{
	/**
	 * Construct a new instance to manipulate the specified menu item
	 * 
	 * @param menuItem the menu item (not <code>null</code>)
	 */
	public SWTMenuItemOperation(MenuItemReference menuItem) {
		super(menuItem);
	}

	/**
	 * Called after an incorrect selection where the menus are no longer visible.
	 * Subclasses may override.
	 * 
	 * @param message the exception message
	 */
	protected void retryAfterBadSelection(String message) {
		// Programmatically trigger the menu item that SHOULD have been selected
		System.out.println(message);
		System.out.println("Programmatically select menu item");
		clickSWTBotStyle();
	}

	/**
	 * Click the menu item programmatically with SWTBot style widget events
	 */
	public SWTMenuItemOperation clickSWTBotStyle() {
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				if (menuItemReference.hasStyle(SWT.CHECK) || menuItemReference.hasStyle(SWT.RADIO)) {
					menuItemReference.setSelection(!menuItemReference.getSelection());
				}
			}
		});
		queueWidgetEvent(menuItemReference.getWidget(), SWT.Selection);
		return this;
	}
}
