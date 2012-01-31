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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;
import abbot.tester.swt.ActionFailedException;

import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.swt.internal.widgets.ListReference;

/**
 * A Selector for Lists.
 */
public class ListSelector extends BasicWidgetSelector {

	
	//the pause between double clicks
//	private static final int CLICK_INTERVAL = 50;
	
	///////////////////////////////////////////////////////////////////////////
	//
	// API
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, java.lang.String, int)
	 */
	public Widget click(Widget w, String itemLabel, int mask) {
		actionClickItem((List)w, itemLabel, mask);
		return w;
	}
	
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#doubleClick(org.eclipse.swt.widgets.Widget, java.lang.String, int)
	 */
	public Widget doubleClick(Widget w, String itemLabel, int mask) {
		actionDoubleClickItem((List)w, itemLabel, mask);
		return w;
	}
	
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#select(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public void select(final Widget w, final int start, final int stop) {
		// TODO[pq]: move into list reference
		w.getDisplay().syncExec(new Runnable() {
			public void run() {
				((List)w).select(start, stop);
			}
		});
	}
	
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#selectAll(org.eclipse.swt.widgets.Widget)
	 */
	public void selectAll(final Widget w) {
		w.getDisplay().syncExec(new Runnable() {
			public void run() {
				((List)w).selectAll();
			}
		});
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Internal
	//
	///////////////////////////////////////////////////////////////////////////
		
	/** Move the mouse pointer over the item at the given index **/
	protected void mouseMoveIndex(final List list, final int index){
		setFocus(list);
		UIProxy.syncExec(list.getDisplay(), new Runnable(){
			public void run(){
				list.setTopIndex(index);
			}
		});
		int topIndex = UIProxy.getTopIndex(list);
		int borderWidth = UIProxy.getBorderWidth(list);
		int itemHeight = UIProxy.getItemHeight(list);
		Rectangle bounds = getGlobalBounds(list);
		Rectangle clientArea = UIProxy.getClientArea(list);
		itemHeight = ListHelper.fixItemHeight(list.getDisplay(), itemHeight);
		// Mac Testing (still not working but on the right track)
		int x;
		if (Platform.isOSX()) {
			int w = clientArea.width / 2;
			x = borderWidth + bounds.x + clientArea.x + Math.min(w, 10);
		} else {
			x = bounds.x+clientArea.width/2;
		}
		mouseMove(x,bounds.y+borderWidth+itemHeight*(index-topIndex)+itemHeight/2);		
		waitForIdle(list.getDisplay());
	}

		
	//!pq:
	private void actionDoubleClickItem(List list, String item, int accelerator){
		String[] items = UIProxy.getItems(list);
		int index = -1;
		for(int i=0; i<items.length;i++){
			if(item.equals(items[i])&&index==-1)
				index = i;
		}
		if(index==-1){
			throw new ActionFailedException("List item \""+item+"\" not found");
		}
		actionDoubleClickIndex(list, index);
	}	
	
	
	/** Click the first occurance of given item based on the given accelerator **/
	private void actionClickItem(List list, String item, int accelerator){
		String[] items = new ListReference(list).getItems();
		int index = -1;
		for(int i=0; i<items.length;i++){
			if(item.equals(items[i])&&index==-1)
				index = i;
		}
		if(index==-1){
			throw new ActionFailedException("List item \""+item+"\" not found");
		}
		actionClickIndex(list,index,accelerator);	
	}
	
	/** Click the item at the given index based on the given accelerator **/
	private void actionClickIndex(List list, int index, int accelerator){
		setFocus(list);
		//System.out.println("moving mouse");
		mouseMoveIndex(list,index);
		//System.out.println("...done moving mouse");
		//System.out.println("waiting for idle");
		waitForIdle(list.getDisplay());
		//System.out.println("...done waiting for idle");
		
		//System.out.println("clicking key");
		
//		boolean shift = (accelerator & SWT.SHIFT) == SWT.SHIFT;
//		boolean ctrl = (accelerator & SWT.CTRL) == SWT.CTRL;
//		boolean alt = (accelerator & SWT.ALT) == SWT.ALT;
//		boolean command = (accelerator & SWT.COMMAND) == SWT.COMMAND;
//		if (shift) {
//			//trace("got shift!");
//			keyDown(SWT.SHIFT);
//		}
//		if (ctrl)
//			keyDown(SWT.CTRL);
//		if (alt)
//			keyDown(SWT.ALT);
//		if (command)
//			keyDown(SWT.COMMAND);
//		
//		accelerator &= (SWT.BUTTON1
//				|SWT.BUTTON2
//				|SWT.BUTTON3);
//		
//		//pause(500);//sanity check need for pause...
//		mousePress(accelerator);
//		mouseRelease(accelerator);
//		//pause(500); //sanity check need for pause...
//		
//		if (ctrl)
//			keyUp(SWT.CTRL);
//		if (shift)
//			keyUp(SWT.SHIFT);
//		if (alt)
//			keyUp(SWT.ALT);
//		if (command)
//			keyUp(SWT.COMMAND);
		
		new SWTMouseOperation(accelerator).execute();

		//!pq:
		if ((list != null) && (!list.isDisposed())) { 
			waitForIdle(list.getDisplay());
		}
	}	
	

	/**
	 * Double click the item at the given index.
	 * @author Markus Kuhn <markuskuhn@users.sourceforge.net>
	 */
	private void actionDoubleClickIndex(List list, int index){
		actionDoubleClickIndex(list,index,SWT.BUTTON1);
	}

	private void actionDoubleClickIndex(List list, int index, int accelerator){
		Display display = list.getDisplay();
		setFocus(list);
		
		mouseMoveIndex(list,index);
		waitForIdle(display);
		
//		mousePress(accelerator);
//		mouseRelease(accelerator);
//		waitForIdle(display);
//		pauseCurrentThread(CLICK_INTERVAL);
//		
//		mousePress(accelerator);
//		mouseRelease(accelerator);
		new SWTMouseOperation(accelerator).count(2).execute();
		
		waitForIdle(display);		
	}	

}
