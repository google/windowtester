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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;

/**
 * A Selector for Tool Items.
 * 
 * @author Phil Quitslund
 */
public class ToolItemSelector extends BasicWidgetSelector {

	private static final int WIN32_MENU_CARET_NUDGE = 8; //this is a bit of a guess; on inspection it "looks" good

	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget click(Widget w, String path) {
		//expand menu
		clickExpand(w);
		//find the menu and click the item
		//return findContextMenuAndClick(w, path);
		return null; //BROKEN!
	}
	
	
	/**
	 * Open the pull down menu associated with this tool item.
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#clickExpand(org.eclipse.swt.widgets.Widget)
	 */
	public Widget clickExpand(Widget w) {
		ToolItem item      = (ToolItem)w;
		int x = getMenuCaretNudge(item);
		int y = 5;
		//TODO: solve the geometry problem more robustly
		click(w, x, y, SWT.BUTTON1);
		//notice: since we have no handle on the menu, we can't wait
		//for a menu expanded condition; hopefully a waitForIdle will suffice:
		//UIDriver.pause(2000);
		waitForIdle(w.getDisplay());
		return w;
	}


	/**
	 * Return a number of pixels to nudge over to click the caret icon that
	 * opens this tool item's menu.
	 */
	private int getMenuCaretNudge(ToolItem item) {
		if (Platform.isOSX()) { // Mac testing...getWidth() is completely inappropriate
			Rectangle b = UIProxy.getBounds(item);
			return b.width - 5;
		}
		//we're seeing intermittent failures; on a hunch, we'll follow suit
		//with the mac approach and use bounds instead of getWidth.
		return UIProxy.getBounds(item).width - WIN32_MENU_CARET_NUDGE;
	}
	 
}
