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

import org.eclipse.swt.widgets.Table;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * Locates columns in {@link Table}s.
 */
public class ColumnLocator implements ILocator, IUISelector, IWidgetLocator {
	
	private TableItemLocator _delegateItemLocator;

	/**
	 * Create a locator instance.
	 * @param column the column number (zero-based)
	 * @param itemLocator the table item locator to wrapper
	 */
	public ColumnLocator(int column, TableItemLocator itemLocator) {
		_delegateItemLocator = itemLocator;
		_delegateItemLocator.setColumn(column);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		return getItemDelegate().click(ui, widget, click);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, IClickDescription click, String menuItemPath) throws WidgetSearchException {
		return getItemDelegate().contextClick(ui, widget, click, menuItemPath);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return getItemDelegate().findAll(ui);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return getItemDelegate().matches(widget);
	}

	TableItemLocator getItemDelegate() {
		return _delegateItemLocator;
	}
	
	
	
	
	
}