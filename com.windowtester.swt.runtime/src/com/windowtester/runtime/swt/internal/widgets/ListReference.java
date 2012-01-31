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
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.List;

import abbot.Platform;
import abbot.tester.swt.WidgetLocator;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.selector.ListHelper;

/**
 * A {@link List} reference.
 */
public class ListReference extends ControlReference<List>{

	
	private class SWTListItemLocation extends SWTLocation {

		private final int index;

		public SWTListItemLocation(int index) {
			super(WTInternal.TOPLEFT);
			this.index = index;
		}
		
		@Override
		protected Rectangle getDisplayBounds() {
			return getBounds(index);
		}
		
		
	}
	
	public ListReference(List control) {
		super(control);
	}

	/**
	 * Proxy for {@link List#getItemCount()}.
	 */
	public int getItemCount() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getItemCount();
			}
		});
	}

	/**
	 * Returns an array of <code>String</code>s which are the items in the receiver's list.
	 * 
	 * @return the items in the receiver's list
	 */
	public String[] getItems() {
		return displayRef.execute(new Callable<String[]>() {
			public String[] call() throws Exception {
				return widget.getItems();
			}
		});
	}
	
	/**
	 * Attempts to select the current item.
	 * 
	 * @return the current selection
	 */
	public String getSelectedItem() {
		return displayRef.execute(new Callable<String>() {
			public String call() throws Exception {
				return widget.getItem(widget.getSelectionIndex());
			}
		});
	}

	/**
	 * Sets the selection to the given index.
	 * 
	 * @return the zero based index of the current selection.
	 */
	public int getSelectionIndex() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getSelectionIndex();
			}
		});
	}
	
	/**
	 * Gets the selected items.
	 * 
	 * @return the selected items in the list.
	 */
	public String[] getSelection() {
		return displayRef.execute(new Callable<String[]>() {
			public String[] call() throws Exception {
				return widget.getSelection();
			}
		});
	}

	public MenuReference showContextMenu(String path) {
		
		setFocus();
		
		final int index = getIndex(path);

		setTopIndex(index);
		
		SWTLocation location = new SWTListItemLocation(index);
	
		SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON3, location, true);
		op.execute();
		return op.getMenu();
		
	}

	public void setTopIndex(final int index) {
		displayRef.execute(new VoidCallable() {
			@Override
			public void call() throws Exception {
				widget.setTopIndex(index);
			}
		});
	}

	private Rectangle getBounds(final int index) {
		int topIndex    = getTopIndex();
		int borderWidth = getBorderWidth();
		int itemHeight  = getItemHeight();
		

		final Rectangle bounds = getGlobalBounds();
		Rectangle clientArea   = getClientArea();
		
		itemHeight = ListHelper.fixItemHeight(widget.getDisplay(), itemHeight);
		
		// Mac Testing (still not working but on the right track)
		int x;
		if (Platform.isOSX()) {
			int w = clientArea.width / 2;
			x = borderWidth + bounds.x + clientArea.x + Math.min(w, 10);
		} else {
			x = bounds.x+clientArea.width/2;
		}
		
		bounds.x = x;
		bounds.y += (borderWidth+itemHeight*(index-topIndex)+itemHeight/2);
		return bounds;
	}

	public int getItemHeight() {
		return displayRef.execute(new Callable<Integer>(){
			public Integer call() throws Exception {
				return widget.getItemHeight();
			}
		});
	}

	private int getBorderWidth() {
		return displayRef.execute(new Callable<Integer>(){
			public Integer call() throws Exception {
				return widget.getBorderWidth();
			}
		});
	}

	public int getTopIndex() {
		return displayRef.execute(new Callable<Integer>(){
			public Integer call() throws Exception {
				return widget.getTopIndex();
			}
		});
	}

	public Rectangle getClientArea() {
		return displayRef.execute(new Callable<Rectangle>(){
			public Rectangle call() throws Exception {
				return widget.getClientArea();
			}
		});
	}
	
	public int getIndex(String path) {
		String[] items = getItems();
		int index = -1;
		for(int i=0; i<items.length;i++){
			if(path.equals(items[i])&&index==-1)
				index = i;
		}
		if (index == -1)
			throw new RuntimeException("Item " + path + " not found in list");
		return index;
	}

	private Rectangle getGlobalBounds() {
		return displayRef.execute(new Callable<Rectangle>() {
			public Rectangle call() throws Exception {
				return WidgetLocator.getBounds(widget, true);
			}
		});
	}
}
