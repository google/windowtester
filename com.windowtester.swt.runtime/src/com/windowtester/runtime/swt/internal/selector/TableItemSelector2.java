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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.finder.FinderUtil;
import com.windowtester.runtime.swt.internal.reveal.IRevealStrategy;
import com.windowtester.runtime.swt.internal.reveal.RevealStrategyFactory;
import com.windowtester.runtime.swt.internal.selector.BasicWidgetSelector;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector2;
import com.windowtester.runtime.swt.internal.selector.PopupMenuSelector.PopupFailedException;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;

/*
 * The other version is kept for leagcey purposes as old UIContext actions
 * depend on its idiosyncarcies.  Moving forward, this is teh prefered tree item selector.
 * 
 * 
 */
public class TableItemSelector2 extends BasicWidgetSelector {

	
	/** How far from the left to nudge a click action */
	public static final int LEFT_NUDGE = 8;

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
			getRevealer().reveal(w, x, y); 
		}
		// this click should be relative to 0 column, coordinates x & y are ignored
		actionClickTableItem((TableItem)w, 0, mask);
		return w;
	}
	
	public Widget click(Widget w, int mask) {
		return click(w, LEFT_NUDGE, 4, mask); //this is a fudged coordinate
	}
	
	public Widget click(int clicks, Widget w, int mask) throws WidgetSearchException {
		return click(clicks, w, 0, new Point(LEFT_NUDGE, 4), mask); //this is a fudged coordinate
	}
	
	
	public Widget click(int clicks, Widget w, int column, Point offset, int mask) throws WidgetSearchException {
		if (w instanceof TableItem) {
			column = column == TableItemLocator.UNSPECIFIED_COLUMN ? 0 : column;
			Point revealOffset = getClickOffset((TableItem)w, column, offset, mask);
			//legacy revealer uses strings to describe paths (in this case columns)
			String colString = "0";
			try {
				colString = Integer.toString(column);
			} catch (NumberFormatException e) {
				LogHandler.log(e);
			}
			getRevealer().reveal(w, colString, revealOffset.x, revealOffset.y); 
			actionClickTableItem(clicks, (TableItem)w, column, offset, mask);
		}
		return w;
	}

	/**
	 * @see com.windowtester.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, IWidgetReference, int)
	 */
	public Widget click(Widget w, String itemLabel, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		
		if (w instanceof TableItem) {
			getRevealer().reveal(w, itemLabel, LEFT_NUDGE, 4); //this is a fudged coordinate
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
	
	
	protected void actionDoubleClickTableItem(final TableItem item, final int columnIndex, final int mask) {
		final Display display = item.getDisplay();
		display.syncExec(new ItemClickProcess(2, item, columnIndex, mask));
		//pauseCurrentThread(500); // wait to allow OS consume events 
	}

	/**
	 * Clicks in the center of the TableItem and the column given.
	 * <p/>
	 * @param item the item to click onto.
	 * @param columnIndex the column to click onto.
	 */
	protected void actionClickTableItem(final TableItem item, final int columnIndex, final int mask) {
		
//		final Display display = item.getDisplay();
//
//		new SystemEventMonitor(UIProxy.getParent(item), SWT.MouseUp){
//			public void syncExecEvents() {
//				display.syncExec(new ItemClickProcess(1, item, columnIndex, mask));
//			}
//		}.run();
		new ItemClickProcess(1, item, columnIndex, mask).run();
	}
	

	/**
	 * Clicks in the center of the TableItem and the column given.
	 * <p/>
	 * @param item the item to click onto.
	 * @param columnIndex the column to click onto.
	 */
	protected void actionClickTableItem(final int numClicks, final TableItem item, final int columnIndex, final Point offset, final int mask) {
//		new SystemEventMonitor(UIProxy.getParent(item), SWT.MouseUp){
//			public void syncExecEvents() {
//				new ItemClickProcess(numClicks, item, columnIndex, offset, mask).run();
//			}
//		}.run();
		new ItemClickProcess(numClicks, item, columnIndex, offset, mask).run();
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
	
	private Point getClickOffset(TableItem item, int columnIndex, Point offset, int mask){
		//fix column 
		columnIndex = (columnIndex == TableItemLocator.UNSPECIFIED_COLUMN) ? 0 : columnIndex;
		boolean check = (mask & SWT.CHECK) == SWT.CHECK;
		if (check){
			// if check is present the offset should be relative to the first column (and xy is ignored)
			Rectangle itemBounds = UIProxy.getBounds(item, 0);
			return new Point(itemBounds.x/2 - 1, itemBounds.y + itemBounds.height/2);
		} else {
			if (offset != null) {
				return offset;	
			}	
			Rectangle itemBounds = UIProxy.getBounds(item, columnIndex);
			return new Point(itemBounds.x + LEFT_NUDGE, itemBounds.y+ itemBounds.height/2);
		}
	}
	
	
	
	private Point getContextClickOffset(TableItem item, int columnIndex, Point offset, int mask){
		//fix column 
		columnIndex = (columnIndex == TableItemLocator.UNSPECIFIED_COLUMN) ? 0 : columnIndex;
		boolean check = (mask & SWT.CHECK) == SWT.CHECK;
		if (check){
			// if check is present the offset should be relative to the first column (and xy is ignored)
			Rectangle itemBounds = UIProxy.getBounds(item, 0);
			return new Point(itemBounds.x/2 - 1, itemBounds.height/2);
		} else {
			if (offset != null) {
				return offset;	
			}	
			Rectangle itemBounds = UIProxy.getBounds(item, columnIndex);
			return new Point(itemBounds.x + LEFT_NUDGE, itemBounds.height/2);
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
		return getClickOffset(item, 0, null, mask);
	}
	
	private IRevealStrategy getRevealer() {
		return _revealer;
	}

	private class ItemClickProcess implements Runnable {
		
		private int clickNum;
		private TableItem item;
		private int columnIndex;
		private int mask;
		private final Point offset;
		
		public ItemClickProcess(int clickNum, TableItem item, int columnIndex, Point offset, int mask){
			this.clickNum = clickNum;
			this.item = item;
			this.columnIndex = columnIndex;
			this.offset = offset;
			this.mask = mask;
		}
		public ItemClickProcess(int clickNum, TableItem item, int columnIndex, int mask){
			this(clickNum, item, columnIndex, null, mask);
		}
		
		
		public void run() {
			final boolean[] isValidColumnIndex = new boolean[1];
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					isValidColumnIndex[0] = item.getParent().getColumnCount() >= columnIndex && columnIndex >= 0;
				}
			});
			if (!isValidColumnIndex[0]) {
				return;
			}
			// get the offset of the click relative to the table
			final Point clickOffset = getClickOffset(item, columnIndex, offset, mask);
			// convert it to Display coordinates
			final Point[] clickLocation = new Point[1];
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					clickLocation[0] = item.getParent().toDisplay(clickOffset);
				}
			});
			// make a click
			click(clickLocation[0].x, clickLocation[0].y, mask, clickNum);
			// causes test not to return when modal dialog opens on double click
			
			//waitForIdle(item.getDisplay());
		}
	}
	
	/**
	 * Returns offset for clicks on columns in table cell
	 * @param item
	 * @param column
	 * @return
	 */
	public Point getTableCellClickOffset(TableItem item, int column){
		return getContextClickOffset(item, column, null, SWT.BUTTON3);
	}

//	public Widget contextClick(TableItem item, int column, Point offset, String menuItemPath) throws WidgetSearchException {
//
//		Control control = FinderUtil.getControl(item);
//		if (control == null)
//			throw new UnsupportedOperationException("Context menus unsupported for widgets of type: " + item.getClass());
//		Widget clicked = null;
//		offset = getContextClickOffset(item, column, offset, SWT.BUTTON3);
//		
//		
//		try {
//			clicked = new PopupMenuSelector2().runPopup(control, item, offset.x, offset.y, menuItemPath);
//			
//		} catch (PopupFailedException e) {
//			throw new WidgetNotFoundException("menu item: " + menuItemPath + " not found in widget " + item);
//		}
//		
//		return clicked;
//	}
}
