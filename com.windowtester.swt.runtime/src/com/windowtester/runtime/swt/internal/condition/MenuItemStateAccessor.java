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
package com.windowtester.runtime.swt.internal.condition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;

import abbot.tester.swt.MenuItemTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.util.PathStringTokenizerUtil;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * Tests enablement and selection of menu items.
 *
 */
public class MenuItemStateAccessor {

	/**
	 * A constant used to specify how many levels of menu to dismiss in
	 * {@link #dismissUnexpectedMenus()}.
	 */
	private static final int MAX_MENU_DEPTH = 5;
	
	private final String menuPath;

	public MenuItemStateAccessor(String menuPath) {
		this.menuPath = menuPath;
	}

	
	//TODO: cache menu depth here
	private String[] tokenize(String path) {
		String[] items = PathStringTokenizerUtil.tokenize(path);
		StringBuffer remainingNodes = new StringBuffer();
		for (int i=1; i < items.length; ) {
			remainingNodes.append(items[i]);
			if (++i < items.length)
				remainingNodes.append('/');
		}
		return new String[]{items[0], remainingNodes.toString()};
	}

	private void dismissMenu(IUIContext ui) {
		//TODO: this may be OS-specific...
		for (int i= 0; i <= MAX_MENU_DEPTH; ++i) {
			ui.keyClick(SWT.ESC); //close menu by hitting ESCAPE
		}
	}


	public boolean isSelected(final IUIContext ui, boolean selected) throws WidgetSearchException {
		MenuItem menuItem = openMenuAndFindItem(ui);
		if (menuItem == null)
			return false;
		return doSelectionTest(menuItem, selected, ui);
	}

	private boolean doSelectionTest(MenuItem menuItem, boolean selected, IUIContext ui) {
		boolean actual = new MenuItemTester().getSelection(menuItem);
		dismissMenu(ui);
		return actual == selected;
	}
	
	
	public boolean isEnabled(IUIContext ui, boolean enabled) throws WidgetSearchException {
		MenuItem menuItem = openMenuAndFindItem(ui);
		if (menuItem == null)
			return false;
		return doEnablementTest(menuItem, enabled, ui);
	}
	
	private boolean doEnablementTest(MenuItem menuItem, boolean enabled, IUIContext ui) {
		boolean actual = new MenuItemTester().getEnabled(menuItem);
		dismissMenu(ui);
		return actual == enabled;
	}
	
	
	private MenuItem openMenuAndFindItem(final IUIContext ui) throws WidgetSearchException {
		final String[] path = tokenize(menuPath);
		final WidgetSearchException[] caughtException = new WidgetSearchException[1]; 
		Thread menuThread = new Thread() {
			public void run() {
				try {
					ui.click(new MenuItemLocator(path[0]));
				} catch (WidgetSearchException e) {
					caughtException[0] = e;
				}
			}
		};
		menuThread.setDaemon(true);
		menuThread.start();

		IWidgetReference ref = (IWidgetReference) ui.find(new SWTWidgetLocator(
				MenuItem.class, path[1]));
		MenuItem menuItem = (MenuItem) ref.getWidget();
		if (menuItem == null)
			return null;
		if (caughtException[0] != null) //TODO there's a timing issue here...
			throw caughtException[0];
		return menuItem;
	}
	


}
