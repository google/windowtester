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
package com.windowtester.runtime.swt.internal.effects;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.BasicFinder;
import abbot.tester.swt.MenuItemTester;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.abbot.matcher.HierarchyMatcher;
import com.windowtester.runtime.swt.internal.abbot.matcher.InstanceMatcher;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;
import com.windowtester.runtime.swt.internal.selector.MenuItemSelector;

public class MenuItemHighlightingSelector extends MenuItemSelector {

	
	private final PlaybackSettings _settings;

	
	public MenuItemHighlightingSelector(PlaybackSettings settings) {
		_settings = settings;
	}


	/**
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget click(Widget w, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		if (w instanceof MenuItem)
			return click((MenuItem)w, path);
		if (w instanceof Menu)
			return click((Menu)w, path);
        throw new UnsupportedOperationException("Widgets of type " + w.getClass() + " not supported.");
	}
	
	
	/**
	 * Click the menu item rooted by this item and described by this path.
	 * @param item - the root item
	 * @param path - the path to the item to click
	 * @return the clicked menu item
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	public Widget click(MenuItem item, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		click(item);
		highlightPause(item.getDisplay());
		Menu menu = new MenuItemTester().getMenu(item);
		if (menu == null)        //if there is no submenu default to clicking just the top level item
			return item;
		return click(menu, path);
	}
	

	/**
	 * Find the menu item with this parent and click it.
	 * @param item - the label of the item to click.
	 * @param parent - the parent Menu or MenuItem
	 * @return the clicked item
	 * @throws WidgetNotFoundException
	 * @throws MultipleWidgetsFoundException
	 */
	protected Widget resolveAndClick(String item, Widget parent) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		
		if (parent instanceof MenuItem)
			parent = new MenuItemTester().getMenu((MenuItem)parent);
		//other case is a menu...
	
		try {
			Widget widget = BasicFinder.getDefault().find(
					new HierarchyMatcher(MenuItem.class, item,
							new InstanceMatcher(parent)));

			mouseMove(widget);
			highlightPause(widget.getDisplay()); //<-- add a pause

			click(widget);
			return widget;
			
		} catch (abbot.finder.swt.WidgetNotFoundException wnfe) {
			//close menu in case of failure
			handleMenuClose();
			//replace/rethrow with our own exception
			throw new WidgetNotFoundException(wnfe.getMessage());
		} catch (abbot.finder.swt.MultipleWidgetsFoundException mwfe) {
			//close menu in case of failure
			handleMenuClose();
			//replace/rethrow with our own exception
			throw new MultipleWidgetsFoundException(mwfe.getMessage());
		}
	}
	
		
	/**
	 * Pause for highlighting.
	 */
	private void highlightPause(Display d) {
		if (_settings.getHighlightingOn())
			pauseCurrentThread(_settings.getHighlightDuration());
	}
	
}
