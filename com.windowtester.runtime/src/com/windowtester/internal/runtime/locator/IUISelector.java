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
package com.windowtester.internal.runtime.locator;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * Interface for the implementation of widget selection behavior. The widget locators can
 * implement this interface to define the behavior specific to that widget.
 */
public interface IUISelector {

	/**
	 * Perform the click.
	 * @param ui the UI context 
	 * @param widget the widget reference to click
	 * @param click a description of the click
	 * @return the clicked widget (as a reference)
	 * @throws WidgetSearchException
	 */
	IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException;
	
	/**
	 * Perform the context click.
	 * @param ui the UI context 
	 * @param widget the widget reference to click
	 * @param click a description of the click
	 * @param menuItemPath the path to the menu item to select
	 * @return the clicked widget (as a reference)
	 * @throws WidgetSearchException
	 */
	IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, IClickDescription click, String menuItemPath) throws WidgetSearchException;


}
