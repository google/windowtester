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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.BasicFinder;
import abbot.tester.swt.MenuItemTester;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.internal.abbot.matcher.HierarchyMatcher;
import com.windowtester.runtime.swt.internal.abbot.matcher.InstanceMatcher;
import com.windowtester.swt.util.PathStringTokenizerUtil;


/**
 * A Selector for Menu Items.
 * 
 * @author Phil Quitslund
 */
public class MenuItemSelector extends BasicWidgetSelector {

	private static final long IDLE_TIMEOUT = 3000; /* just a guess */
	
	private final class SWTIdleConditionWithTimeout extends SWTIdleCondition {
		private final Display display;
		private long timeout = IDLE_TIMEOUT;

		private SWTIdleConditionWithTimeout(Display display) {
			super(display);
			this.display = display;
		}

		public void waitForIdle() {
			final long now = System.currentTimeMillis();
			while (!timedOut(now) && !test()) {
				if (display.getThread() != Thread.currentThread()) {
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						// ignored
					}
				}
			}
		}

		private boolean timedOut(long now) {
			boolean timedOut = System.currentTimeMillis() - now > timeout;
			if (timedOut)
				TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "wait for idle in menu selection timeout exceeded (" + timeout + ") proceeding...");
			return timedOut;
		}
	}

	private int clickCount;

	
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
		MenuItemTester tester = new MenuItemTester();
		// get the menu and its parent before clicking in case the menu is disposed early
		Menu menu = tester.getMenu(item);
		Menu parent = null;
		if (menu == null)
			parent = tester.getParent(item);
		click(item);
		
		// if there is no submenu default to clicking just the top level item
		if (menu == null){        
			// [Dan] Do we have a test for this? Can this special case code be removed?
			if(SWT.getPlatform().equals("gtk")&&((UIProxy.getStyle(parent) & SWT.BAR)!=0)){
				waitForIdle(item.getDisplay());
				pauseDisplayThread(item.getDisplay(), 500);
				click(item);
				waitForIdle(item.getDisplay());
			}
			return item;
		}
		
		return click(menu, path);
	}
	
	/**
	 * @see com.windowtester.event.selector.swt.BasicWidgetSelector#openToolItemMenu(org.eclipse.swt.widgets.Widget)
	 */
	public synchronized Widget click(Widget w) {
		/*
		 * Click and increment our counter.
		 */
		Widget clicked = super.click(w);
		++clickCount;
		return clicked;
	}
	
	
	/**
	 * Click the menu item contained by this menu and described by this path.
	 * @param menu - the containing menu
	 * @param path - the path to the item to click
	 * @return the clicked menu item
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	public Widget click(Menu menu, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		
		trace("click: " + path);

		//fixing to handle escaped '\'s
		//String[] items = path.split(DELIM);
		String[] items = PathStringTokenizerUtil.tokenize(path);
		
		Widget clicked = menu;
		for (int i = 0; i < items.length; i++) {
			String item = items[i];
			clicked = resolveAndClick(item, clicked);
		}
		//reset count
		clickCount = 0;
		
		return clicked;
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
		
		Widget widget;
		try {
			widget = BasicFinder.getDefault().find(new HierarchyMatcher(MenuItem.class, item, new InstanceMatcher(parent)));
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
	 * Close open menus (called on error cases).
	 *
	 */
	protected void handleMenuClose() {
		
		//takeScreenShot(); //BEFORE closing menu
		
		//TODO: this may be OS-specific...
		for (int i= 0; i <= clickCount; ++i) {
			//System.err.println("ESC");
			keyClick(SWT.ESC); //close menu by hitting ESCAPE
		}
	}

	
	//Override to allow for timeouts to protect native dialog case
	protected /*synchronized*/ void waitForIdle(final Display display){
		
		/*
		 * Fix for spawned native dialogs which block the idle condition.
		 * TODO: this is really just a best guess strat -- and could be revisited
		 */
		
		//provisional fix for  Dialogs Opened During Window Tester Widget Selector Actions Cause Hangs
		//new SWTIdleCondition(display).waitForIdle();
			
		//remedy issues with native dialogs opening and blocking idle
		
		new SWTIdleConditionWithTimeout(display).waitForIdle();
			
	}

	
	
}
