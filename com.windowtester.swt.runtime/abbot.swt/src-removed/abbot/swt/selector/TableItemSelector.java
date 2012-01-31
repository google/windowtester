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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.reveal.IRevealStrategy;
import com.windowtester.runtime.swt.internal.reveal.RevealStrategyFactory;

/**
 * Legacy Selector.  Used in old UIContext.  Does strange things (like ignoring xys, etc.
 *
 * @author Phil Quitslund
 * @deprecated
 *
 */
public class TableItemSelector extends BasicWidgetSelector {

	
	/** How far from the left to nudge a click action */
	private static final int LEFT_NUDGE = 8;

	private final IRevealStrategy _revealer = RevealStrategyFactory.getRevealer(TableItem.class);

	///////////////////////////////////////////////////////////////////////////
	//
	// API
	//
	///////////////////////////////////////////////////////////////////////////

	
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, int, int, int)
	 */
	public Widget click(Widget w, int x, int y, int mask) {
		if (w instanceof TableItem) {
			_revealer.reveal(w, LEFT_NUDGE, 4); //this is a fudged coordinate
		}
		// this click should be relative to 0 column, coordinates x & y are ignored
		actionClickTableItem((TableItem)w, 0, mask);
		return w;
	}
	
	/**
	 * @see com.windowtester.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, IWidgetReference, int)
	 */
	public Widget click(Widget w, String itemLabel, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		
		if (w instanceof TableItem) {
			_revealer.reveal(w, itemLabel, LEFT_NUDGE, 4); //this is a fudged coordinate
		}
		
		int columnIndex = 0;
		try {
			columnIndex = Integer.parseInt(itemLabel);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(itemLabel + " must be an integer value to select the item column");
		}
			
		actionClickTableItem((TableItem)w, columnIndex, mask);
		return w;
	}
	
	/**
	 * @see com.windowtester.swt.ISWTWidgetSelectorDelegate#doubleClick(org.eclipse.swt.widgets.Widget, java.lang.String, int)
	 */
	public Widget doubleClick(Widget w, String itemLabel, int mask) {
		int columnIndex = 0;
		try {
			columnIndex = Integer.parseInt(itemLabel);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(itemLabel + " must be an integer value to select the item column");
		}
		
		actionDoubleClickTableItem((TableItem)w, columnIndex, mask);
		return w;
	}
	
	
	private void actionDoubleClickTableItem(final TableItem item, final int columnIndex, final int mask) {
		final Display display = item.getDisplay();
		display.syncExec(new ItemClickProcess(item, columnIndex, mask, 2));
		//pauseCurrentThread(500); // wait to allow OS consume events 
	}

	/**
	 * Clicks in the center of the TableItem and the column given.
	 * <p/>
	 * @param item the item to click onto.
	 * @param columnIndex the column to click onto.
	 */
	public void actionClickTableItem(final TableItem item, final int columnIndex, final int mask) {
		
//		final Display display = item.getDisplay();
//
//		new SystemEventMonitor(UIProxy.getParent(item), SWT.MouseUp){
//			public void syncExecEvents() {
//				display.syncExec(new ItemClickProcess(item, columnIndex, mask, 1));
//			}
//		}.run();
		new ItemClickProcess(item, columnIndex, mask, 1).run();
		
		//pauseCurrentThread(500); // wait to allow OS consume events
	}
	

	
	/**
	 * Calculate the offset for clicking a table item.
	 */
	public Point getClickOffset(Rectangle rect, int mask) {
		/*
		 * center clicking does not work...
		 * (For example, clicking the Debug item in the Open New Perspective Wizard)
		 * Our stop-gap is to click the far left of the item (thinking that it would have to be 
		 * REALLY tiny to miss.
		 */
		return new Point(LEFT_NUDGE, rect.height/2); 
	}
	
	private Point getClickOffset(TableItem item, int columnIndex, int mask){
		boolean check = (mask & SWT.CHECK) == SWT.CHECK;
		if(check){
			// if check is present the offset should be relative to the first column
			Rectangle itemBounds = UIProxy.getBounds(item, 0);
			return new Point(itemBounds.x/2 - 1, itemBounds.y + itemBounds.height/2);
		}else{
			Rectangle itemBounds = UIProxy.getBounds(item, columnIndex);
			return new Point(itemBounds.x+LEFT_NUDGE, itemBounds.y + itemBounds.height/2);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.event.selector.swt.BasicWidgetSelector#getClickOffset(org.eclipse.swt.widgets.Widget)
	 */
	public Point getClickOffset(Widget w, int mask) {
		if (!(w instanceof TableItem)) {
			LogHandler.log("unexpected class " + w.getClass() + " in TableItemSelector.getClickOffset()");
			//log but move on...
		}
		TableItem item = (TableItem)w;
		// this only make sense when columnIndex = 0
		return getClickOffset(item, 0, mask);
	}
	
	private class ItemClickProcess implements Runnable {
		
		private int clickNum;
		private TableItem item;
		private int columnIndex;
		private int mask;
		
		public ItemClickProcess(TableItem item, int columnIndex, int mask, int clickNum){
			this.clickNum = clickNum;
			this.item = item;
			this.columnIndex = columnIndex;
			this.mask = mask;
		}
		public void run() {
			if (item.getParent().getColumnCount() < columnIndex || columnIndex < 0) {
				return;
			}
			// get the offset of the click relative to the table
			Point clickOffset = getClickOffset(item, columnIndex, mask);
			// convert it to Display coordinates
			final Point clickLocation = item.getParent().toDisplay(clickOffset);
			// make a click
			click(clickLocation.x, clickLocation.y, mask, clickNum);
			waitForIdle(item.getDisplay());
		}
	}
}
