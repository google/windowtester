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
package com.windowtester.swt.runtime.internal.macosx.cocoa;

import java.util.List;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.TabItem;

import com.windowtester.swt.runtime.internal.macosx.MacExtensions;

/**
 * All methods must be executed on the UI thread.
 */
public abstract class MacCocoa implements MacExtensions {

	protected static final int kCFAllocatorDefault = 0;
	protected static final String kAXChildrenAttribute = "AXChildren";
	protected static final String kAXPositionAttribute = "AXPosition";
	protected static final String kAXSizeAttribute = "AXSize";

	private static final String MENU_BAR_FONT_NAME = "Lucinda Grande";
	
	
	/**
	 * This value will be over-written at initialization time to accommodate different font sizes
	 * associated with different monitor resolutions. 
	 * 
	 * @see {@link #fixMenuBarFontSize()}.
	 */
	public static int MENU_BAR_FONT_SIZE = 12;
	
	private static final int MENU_BAR_ITEM_HORIZ_PADDING = 21;
	private static final int MENU_BAR_ITEM_VERT_PADDING = 3;
	private static final int APPLE_MENU_OFFSET_X = 45;
	private static final String APP_NAME_PROPERTY = "appName";
	private static final String DEFAULT_APP_NAME = "Eclipse";

	/**
	 * Initialize the menu bar font size based on the primary monitor's screen resolution.
	 * 
	 */
	public static void fixMenuBarFontSize() {
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				Monitor monitor = Display.getDefault().getPrimaryMonitor();
				Rectangle bounds = monitor.getBounds();
				if (bounds.width >= 1440) {
					MENU_BAR_FONT_SIZE = 12;
				} else {
					/*
					 * TODO: fill out other possible values for different monitor resolutions
					 */
					MENU_BAR_FONT_SIZE = 14;				
				}
			}
		});
	}
	
	/**
	 * Given a MenuItem, return its bounding box.
	 * 
	 * @param item the menu item
	 * @return Rectangle of item (in global coordinates), or null if something didn't work
	 */
	public abstract Rectangle getMenuItemBounds(MenuItem menuItem);

	/**
	 * Given a TabItem, return its bounding box.
	 * <p>
	 * Not used in Cocoa.
	 * 
	 * @param item the tab item
	 * @return Rectangle of item (in global coordinates), or null if something didn't work
	 */
	public Rectangle getTabItemBounds(TabItem item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return true if the accessibility API is enabled.
	 * <p>
	 * To enable it: open System Preferences, select Universal Access, then
	 * select "Enable access for assistive devices".
	 * <p>
	 * NB: Accessibility is not needed in Cocoa.
	 * 
	 * @return true if the accessibility API is enabled
	 */
	public abstract boolean isAXAPIEnabled();

	/**
	 * @return <code>true</code> if the given menu item appears in the menu bar.
	 */
	protected static boolean isMenuBarItem(MenuItem item) {
		return isMenuBar(item.getParent());
	}

	/**
	 * Fills the {@link List} of {@link Rectangle} with
	 * items bounds of menu bar in <code>bounds</code> parameter.
	 * 
	 * @param menu the {@link Menu} with style {@link SWT#BAR}.
	 * @param bounds the {@link List} of {@link Rectangle} to fill with items bounds values.
	 */
	protected void getMenuBarVisualData(Menu menu, List<Rectangle> bounds) {
		Point ext = getApplicationMenuLabelExtent();
		int height = ext.y;
		int menuWidth = ext.x + APPLE_MENU_OFFSET_X;
		for (int i = 0; i < menu.getItemCount(); ++i) {
			MenuItem item = menu.getItem(i);
			String text = item.getText();
			int itemWidth = 5;
			if (text != null) {
				Point textDimensions = getMenuBarLabelExtent(text);
				itemWidth = textDimensions.x;
			}
			bounds.add(new Rectangle(menuWidth, 0, itemWidth, height));
			menuWidth += itemWidth;
		}
	}

	/**
	 * Return the menu bar height.
	 */
	protected abstract int getMenuBarHeight();

	private String getApplicationName() {
		IProduct product = Platform.getProduct();
		String appName = product == null ? DEFAULT_APP_NAME : product.getProperty(APP_NAME_PROPERTY);
		return appName.length() == 0 ? DEFAULT_APP_NAME : appName;
	}

	private Point getApplicationMenuLabelExtent() {
		Point ext = getMenuLabelExtent(getApplicationName(), SWT.BOLD);
		return new Point(ext.x + MENU_BAR_ITEM_HORIZ_PADDING, ext.y + MENU_BAR_ITEM_VERT_PADDING);
	}

	private Point getMenuBarLabelExtent(String label) {
		Point ext = getMenuLabelExtent(label, SWT.NORMAL);
		return new Point(ext.x + MENU_BAR_ITEM_HORIZ_PADDING, ext.y + MENU_BAR_ITEM_VERT_PADDING);
	}

	private Point getMenuLabelExtent(String label, int style) {
		Display d = Display.getCurrent();
		Font f = new Font(d, MENU_BAR_FONT_NAME, MENU_BAR_FONT_SIZE, style);
		GC gc = new GC(d);
		gc.setFont(f);
		Point pt = gc.stringExtent(label);
		gc.dispose();
		return pt;
	}

	/**
	 * @return <code>true</code> if the given menu is the menu bar.
	 */
	protected static boolean isMenuBar(Menu menu) {
		return (menu.getStyle() & SWT.BAR) != 0;
	}
}
