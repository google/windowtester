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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.swt.internal.operation.SWTTableColumnLocation;
import com.windowtester.runtime.swt.internal.reveal.IRevealStrategy;
import com.windowtester.runtime.swt.internal.reveal.RevealStrategyFactory;

public class TableColumnSelector extends BasicWidgetSelector {
	
	private final IRevealStrategy _revealer = RevealStrategyFactory.getRevealer(TableColumn.class);
	
	///////////////////////////////////////////////////////////////////////////
	//
	// API
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, int, int, int)
	 */
	public Widget click(Widget w, int x, int y, int mask) {
		if (w instanceof TableColumn) {
			getRevealer().reveal(w, x, y); 
		}
		// this click should be relative to 0 column, coordinates x & y are ignored
		actionClickTableColumn((TableColumn)w, mask);
		return w;
	}
	
	public Widget click(int clicks, Widget w, Point offset, int mask) throws WidgetSearchException {
		Point revealOffset = getClickOffset((TableColumn)w, offset, mask);
		
		getRevealer().reveal(w, revealOffset.x, revealOffset.y); 
		actionClickTableItem(clicks, (TableColumn)w, offset, mask);
	
		return w;
	}
	
	protected void actionClickTableColumn(final TableColumn item, final int mask) {
//		
//		final Display display = item.getDisplay();
//
//		new SystemEventMonitor(UIProxy.getParent(item), SWT.MouseUp){
//			public void syncExecEvents() {
//				display.syncExec(new ItemClickProcess(1, item, mask));
//			}
//		}.run();
		new ItemClickProcess(1, item, mask).run();
	}

	
	protected void actionClickTableItem(final int numClicks, final TableColumn item, final Point offset, final int mask) {
		
//		final Display display = item.getDisplay();
//
//		new SystemEventMonitor(UIProxy.getParent(item), SWT.MouseUp){
//			public void syncExecEvents() {
//				display.syncExec(new ItemClickProcess(numClicks, item, offset, mask));
//			}
//		}.run();
		new ItemClickProcess(numClicks, item, offset, mask).run();
	}
	
	
	private IRevealStrategy getRevealer() {
		return _revealer;
	}
	
	
	private Point getClickOffset(TableColumn item, Point offset, int mask){
		if (offset != null) {
			return offset;	
		}	
		Rectangle itemBounds = UIProxy.getBounds(item);
		return new Point(itemBounds.x, itemBounds.y+ itemBounds.height/2);
		
	}
	
	
	
	private class ItemClickProcess implements Runnable {
		
		private int clickNum;
		private TableColumn item;
		private int mask;
		private final Point offset;
		
		public ItemClickProcess(int clickNum, TableColumn item, Point offset, int mask){
			this.clickNum = clickNum;
			this.item = item;
			this.offset = offset;
			this.mask = mask;
		}
		public ItemClickProcess(int clickNum, TableColumn item, int mask){
			this(clickNum, item, null, mask);
		}
		
		
		public void run() {
			// get the offset of the click relative to the table
//			Point clickOffset = getClickOffset(item, offset, mask);
			// convert it to Display coordinates
//			final Point clickLocation = item.getParent().toDisplay(clickOffset);
			// make a click
			new SWTMouseOperation(mask).at(new SWTTableColumnLocation(item, WTInternal.LEFT).offset(offset)).count(clickNum).execute();
		}
	}
	
	


}
