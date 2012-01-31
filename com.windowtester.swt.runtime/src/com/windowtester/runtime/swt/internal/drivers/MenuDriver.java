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
package com.windowtester.runtime.swt.internal.drivers;

import java.util.concurrent.Callable;

import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector2;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.StringComparator;

/**
 * Given a path, this class drives menu selection by interacting with menu and menu item
 * references. This class collects state and thus should be re-used.
 */
public class MenuDriver
{

	private static final int RETRY_LIMIT = 3;

	/** Used internally by {@link #getNextMenuItem(MenuReference, String)} */
	private MenuItemReference[] items;

	/** Used internally by {@link #getNextMenuItem(MenuReference, String)} */
	private String[] textForItems;

	/**
	 * Find the menu item(s) specified by the menu item path and select each one in
	 * succession. Assume that the menu specified in the arguments is already visible but
	 * nested menus are not.
	 * 
	 * @param menu the menu containing the first item to be selected.
	 * @return the last menu item that was selected
	 */
	public MenuItemReference resolveAndSelect(Callable<MenuReference> showFirstMenu, String menuItemPath)
		throws WidgetNotFoundException, MultipleWidgetsFoundException
	{
		int millisecondPauseBetweenClicks = 0;
		int retryCount = 0;
		while (true) {
			Exception exception;
			try {
				return resolveAndSelect0(showFirstMenu, menuItemPath, millisecondPauseBetweenClicks);
			}
			catch (Exception e) {
				exception = e;
			}
			retryCount++;
			System.out.println("Failed to select menu item\n   menu item path: " + menuItemPath + "\n   exception: "
				+ exception + "\n   attempt " + retryCount + " of " + RETRY_LIMIT);
			if (retryCount >= RETRY_LIMIT) {
				if (exception instanceof WidgetNotFoundException)
					throw (WidgetNotFoundException) exception;
				if (exception instanceof MultipleWidgetsFoundException)
					throw (MultipleWidgetsFoundException) exception;
				if (exception instanceof RuntimeException)
					throw (RuntimeException) exception;
				// Should not reach here, but just to be safe
				throw new RuntimeException(exception);
			}
			millisecondPauseBetweenClicks += 2000;
		}
	}

	private MenuItemReference resolveAndSelect0(Callable<MenuReference> showFirstMenu, String menuItemPath, int millisecondPauseBetweenClicks)
		throws Exception
	{
		pause(millisecondPauseBetweenClicks);
		MenuReference menu = showFirstMenu.call();
		if (menu == null)
			throw new IllegalArgumentException("menu cannot be null");
		PathString path = new PathString(menuItemPath);
		try {
			MenuItemReference itemRef;

			// Click each cascading menu item and wait for the menu to appear

			while (true) {
				pause(millisecondPauseBetweenClicks);
				itemRef = getNextMenuItem(menu, path.next());
				if (!path.hasNext())
					break;
				menu = itemRef.showMenu();
				if (menu == null)
					throw new IllegalStateException("Failed to show menu for " + itemRef + "\n   menu item path: "
						+ menuItemPath);
			}

			// Select the last menu item using a standard click

			select(itemRef);

			return itemRef;
		}

		// If any exception occurs, take a screenshot and close any open menus

		catch (Exception e) {
			ScreenCapture.createScreenCapture();
			DisplayReference.getDefault().closeAllMenus();
			throw e;
		}
	}

	/**
	 * Can be customized in subclasses.
	 */
	protected void select(MenuItemReference itemRef) {
		itemRef.click();

		/*
		 * Alternately, select the menu item programmatically, but in testing, 
		 * the SWTBot style selection does not dismiss the menu which causes problems.
		 * Must dismiss the menus first and then programmatically select menu item
		 */
		// DisplayReference.getDefault().closeAllMenus();
		// System.out.println("menu item not visible, selecting programmatically");
		// new SWTMenuItemOperation(itemRef).clickSWTBotStyle().execute();
	}

	private void pause(int milliseconds) {
		if (milliseconds > 0) {
			try {
				Thread.sleep(milliseconds);
			}
			catch (InterruptedException e) {
				//ignore interrupt
			}
		}
	}

	/**
	 * Search the specified menu for an item with text matching specified text
	 * 
	 * @param menu the menu containing items to be searched
	 * @param rawItemText the text to be matched
	 * @return the menu item with text matching the specified text
	 */
	private MenuItemReference getNextMenuItem(final MenuReference menu, String rawItemText)
		throws WidgetNotFoundException, MultipleWidgetsFoundException
	{
		String itemText = StringUtils.trimMenuText(rawItemText);

		// Get the menu items and their associated text

		menu.getDisplayRef().execute(new VoidCallable() {
			public void call() throws Exception {
				items = menu.getItems();
				textForItems = new String[items.length];
				for (int i = 0; i < items.length; i++) {
					textForItems[i] = items[i].getTextForMatching();
					// System.out.println(textForItems[i] + " : " + items[i].getDisplayBounds() + " parent : " + items[i].getParent().getDisplayBounds());
				}
			}
		}, 30000);

		// Look for an exact match

		MenuItemReference found = null;
		for (int i = 0; i < textForItems.length; i++) {
			if (itemText.equals(textForItems[i])) {
				if (found == null)
					found = items[i];
				else
					throw new MultipleWidgetsFoundException("Multiple menu items found for \'" + itemText + "\' in "
						+ getAllItemText(textForItems));
			}
		}
		if (found != null)
			return found;

		// Look for a pattern match

		for (int i = 0; i < textForItems.length; i++) {
			if (StringComparator.matches(StringUtils.trimMenuText(textForItems[i]), itemText)) {
				if (found == null)
					found = items[i];
				else
					throw new MultipleWidgetsFoundException("Multiple menu items found for \'" + itemText + "\' in "
						+ getAllItemText(textForItems));
			}
		}
		if (found != null)
			return found;

		throw new WidgetNotFoundException("No menu item found for \'" + itemText + "\' in "
			+ getAllItemText(textForItems));
	}

	/**
	 * Answer all text used in the text comparison as a single string.
	 * 
	 * @param textForItems an array of text
	 * @return a string for inclusion in a {@link WidgetNotFoundException} or
	 *         {@link MultipleWidgetsFoundException} message
	 */
	private StringBuilder getAllItemText(String[] textForItems) {
		StringBuilder allItemText = new StringBuilder(128);
		for (int i = 0; i < textForItems.length; i++) {
			allItemText.append('\n');
			allItemText.append(textForItems[i]);
		}
		return allItemText;
	}
}
