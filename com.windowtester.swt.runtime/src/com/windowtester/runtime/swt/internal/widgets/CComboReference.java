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

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.eclipse.swt.custom.CCombo;

import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.operation.SWTCComboOperation;

/**
 * A {@link CCombo} reference.
 */
public class CComboReference extends ControlReference<CCombo> {

	public CComboReference(CCombo control) {
		super(control);
	}

	/**
	 * Gets the item count in the combo box.
	 * 
	 * @return the number of items in the combo box.
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
	 * @return the current selection in the combo box.
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
	
	public void click(final String item) throws WidgetSearchException {	
		new SWTCComboOperation(this).select(indexOf(item)).execute();
	}
	
	/**
	 * Answer the index for the specified item
	 * 
	 * @param item the item to be found
	 * @return the index of the specified item
	 * @throws WidgetNotFoundException thrown if the specified item cannot be found
	 */
	public int indexOf(final String item) throws WidgetNotFoundException {
		String[] items = getItems();
		int indexOf = Arrays.asList(items).indexOf(item);
		if (indexOf >= 0)
			return indexOf;
		String errMsg = "Item '" + item + "' not found in ccombo box containing";
		for (String eachItem : items)
			errMsg += "\n\t" + eachItem;
		throw new WidgetNotFoundException(errMsg);
	}
	
	/**
	 * Proxy for {@link CCombo#select(int)}.
	 */
	public void select(final int index){
		displayRef.execute(new VoidCallable() {
			public void call() throws Exception {
				widget.select(index);
			}
		});
	}
	
	
}
