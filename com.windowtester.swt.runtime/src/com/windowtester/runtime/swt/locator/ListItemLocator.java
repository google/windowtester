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
package com.windowtester.runtime.swt.locator;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.List;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.locator.VirtualItemLocator;
import com.windowtester.runtime.swt.internal.selector.ListSelector;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.internal.widgets.ListReference;
import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link List} items.
 */
public class ListItemLocator extends VirtualItemLocator {
	
	private static final long serialVersionUID = 6288630087493333016L;

	/** 
	 * Create a locator instance for the common case where no information is needed
	 * to disambiguate the parent control.
	 * <p>
	 * This convenience constructor is equivalent to the following:
	 * <pre>
	 * new ListItemLocator(itemText, new SWTWidgetLocator(List.class));
	 * </pre>
	 * 
	 * @param itemText the list item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public ListItemLocator(String itemText) {
		super(List.class, itemText);
	}

	//child
	/** 
	 * Create a locator instance.
	 * @param text the list item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public ListItemLocator(String text, IWidgetLocator parent) {
		super(List.class, text, SWTWidgetLocator.adapt(parent));
	}

	//indexed child
	/** 
	 * Create a locator instance.
	 * @param text the list item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator
	 */
	public ListItemLocator(String text, int index, IWidgetLocator parent) {
		super(List.class, text, index, SWTWidgetLocator.adapt(parent));
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		ListReference listRef = (ListReference)ui.find(this);
		//List list = (List) listLocator.getWidget();
		//Widget clicked = null;
		int clicks = click.clicks();
		preClick(listRef, null, ui);
		List list = listRef.getWidget();
		if (clicks == 1)
			new ListSelector().click(list, getPath(), click.modifierMask());
		else if (clicks == 2)
			new ListSelector().doubleClick(list, getPath(), click.modifierMask());
		else
			throw new UnsupportedOperationException("clicks: " + clicks + " unsupported");
		
		postClick(listRef, ui);
		return listRef;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, final IWidgetReference widget, final IClickDescription click, String menuItemPath) throws WidgetSearchException {
		final ListReference listRef = (ListReference)ui.find(this);
		MenuItemReference clicked = new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return ((ListReference) listRef).showContextMenu(getPath());
			}
		}, menuItemPath);
		return WidgetReference.create(clicked, this);
	}

	
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// IsVisibleLocator
	//
	////////////////////////////////////////////////////////////////////////////
	
	public boolean isVisible(IUIContext ui) throws WidgetSearchException {
		IWidgetLocator[] locators = ui.findAll(this);
		IWidgetReference reference;
		List list;
		String[] listStrItems;
		
		if(locators.length == 1 && locators[0] != null && locators[0] instanceof IWidgetReference) {
			reference = (IWidgetReference) locators[0];
			if(reference.getWidget() != null && reference.getWidget() instanceof List) {
				list = (List) reference.getWidget();
				listStrItems = UIProxy.getItems(list);
				for (int i = 0; listStrItems != null && i < listStrItems.length; i++) {
					if(listStrItems[i].equals(getPath())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
