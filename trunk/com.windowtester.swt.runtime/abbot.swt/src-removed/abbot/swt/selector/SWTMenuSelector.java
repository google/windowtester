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
package com.windowtester.runtime.swt.internal.selector;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.abbot.matcher.NameOrLabelMatcher;
import com.windowtester.runtime.swt.internal.operation.SWTKeyOperation;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuBarOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowViewMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;
import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;
import com.windowtester.runtime.swt.internal.widgets.ToolItemReference;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.StringComparator;
import com.windowtester.swt.util.PathStringTokenizerUtil;

/**
 * A consolidation of the {@link PopupMenuSelector2} and {@link MenuItemSelector} classes
 * 
 * @author Dan Rubel
 */
public class SWTMenuSelector
{
	/**
	 * Select the specified top level menu item(s) from the active window's menu bar
	 * 
	 * @param menuItemPath the slash separated path indicating the menu item(s) to be
	 *            selected
	 * @return the last menu item that was selected
	 */
	public MenuItemReference selectMenu(String menuItemPath) throws MultipleWidgetsFoundException, WidgetNotFoundException {
		return resolveAndSelect(new SWTShowMenuBarOperation().openMenuBar(), menuItemPath);
	}

	/**
	 * Open the menu associated with the specified tool item and select the specified menu
	 * item(s)
	 * 
	 * @param itemRef the tool item with the associated menu
	 * @param menuItemPath the slash separated path indicating the menu item(s) to be
	 *            selected
	 * @return the last menu item that was selected
	 */
	public MenuItemReference selectToolItemMenu(ToolItemReference itemRef, String menuItemPath) throws MultipleWidgetsFoundException,
		WidgetNotFoundException
	{
		return resolveAndSelect(new SWTShowMenuOperation().openMenuClick(WT.BUTTON1, new SWTWidgetLocation(itemRef, WTInternal.RIGHT).offset(-3, 0), false), menuItemPath);
	}

	/**
	 * Open the menu associated with the specified view and select the specified menu
	 * item(s)
	 * 
	 * @param viewId the identifier of the view
	 * @param menuItemPath the slash separated path indicating the menu item(s) to be
	 *            selected
	 * @return the last menu item that was selected
	 */
	public MenuItemReference selectViewMenu(String viewId, String menuItemPath) throws MultipleWidgetsFoundException,
		WidgetNotFoundException
	{
		return resolveAndSelect(new SWTShowViewMenuOperation().openViewMenu(viewId), menuItemPath);
	}

	/**
	 * Perform a context click at the specified location and select the specified menu
	 * item(s)
	 * 
	 * @param location the location at which the right click should occur to open the
	 *            context menu
	 * @param pauseOnMouseDown <code>true</code> if the mouse down event should be
	 *            processed before the mouse up event is posted.
	 * @param menuItemPath the slash separated path indicating the menu item(s) to be
	 *            selected
	 * @return the last menu item that was selected
	 */
	public MenuItemReference contextClick(SWTLocation location, boolean pauseOnMouseDown, String menuItemPath) throws MultipleWidgetsFoundException,
		WidgetNotFoundException
	{
		return resolveAndSelect(new SWTShowMenuOperation().openMenuClick(WT.BUTTON3, location, pauseOnMouseDown), menuItemPath);
	}

	/**
	 * Find the menu item(s) specified by the menu item path and select each one in
	 * succession. This method assumes that the operation specified, when executed by this
	 * method, opens the menu containing the first item to be selected.
	 * 
	 * @param firstOp the operation that opens the menu containing the first item to be
	 *            selected.
	 * @param menuItems the menu items in the currently open menu
	 * @return the last menu item that was selected
	 */
	private MenuItemReference resolveAndSelect(SWTShowMenuOperation firstOp, String menuItemPath)
		throws MultipleWidgetsFoundException, WidgetNotFoundException
	{
		String[] elements = PathStringTokenizerUtil.tokenize(menuItemPath);
		int index = 0;
		try {
			firstOp.execute();
			MenuItemReference itemRef = getNextMenuItem(firstOp.getMenuItems(), elements[index]);
			index++;

			// Click each cascading menu item and wait for the menu to appear

			while (index < elements.length) {
				SWTShowMenuOperation op = new SWTShowMenuOperation();
				op.openMenuClick(WT.BUTTON1, new SWTWidgetLocation(itemRef, WTInternal.CENTER), false).execute();
				itemRef = getNextMenuItem(op.getMenuItems(), elements[index]);
				index++;
			}

			// Select the last menu item using a standard click

			SWTMouseOperation op = new SWTMouseOperation(WT.BUTTON1);
			op.at(new SWTWidgetLocation(itemRef, WTInternal.CENTER));
			op.execute();
			index++;
			return itemRef;
		}

		// If any exception occurs, take a screenshot and close any open menus

		finally {
			if (index <= elements.length) {
				ScreenCapture.createScreenCapture();
				// Close any open menus
				while (index > 0) {
					new SWTKeyOperation().keyCode(WT.ESC).execute();
					index--;
				}
			}
		}
	}

	/**
	 * Search the specified menu items for an item with text matching specified text
	 * 
	 * @param items the menu items to be searched
	 * @param itemText the text to be matched
	 * @return the menu item with text matching the specified text
	 */
	private MenuItemReference getNextMenuItem(MenuItemReference[] items, String itemText) throws MultipleWidgetsFoundException,
		WidgetNotFoundException
	{
		// Get menu item text
		
		StringBuilder allItemText = new StringBuilder(128);
		String[] textForItems = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			String text = items[i].getText();
			textForItems[i] = text;
			allItemText.append('\n');
			allItemText.append(text);
		}
		
		// Look for an exact match
		
		MenuItemReference found = null;
		for (int i = 0; i < textForItems.length; i++) {
			if (itemText.equals(NameOrLabelMatcher.trimMenuText(textForItems[i]))) {
				if (found == null)
					found = items[i];
				else
					throw new MultipleWidgetsFoundException("Multiple items found for " + itemText + " in " + allItemText);
			}
		}
		if (found != null)
			return found;
		
		// Look for a pattern match

		for (int i = 0; i < textForItems.length; i++) {
			if (StringComparator.matches(textForItems[i], itemText)) {
				if (found == null)
					found = items[i];
				else
					throw new MultipleWidgetsFoundException("Multiple items found for " + itemText + " in " + allItemText);
			}
		}
		if (found != null)
			return found;

		throw new WidgetNotFoundException("No item found for " + itemText + " in " + allItemText);
	}
}
