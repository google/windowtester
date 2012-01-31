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
package com.windowtester.runtime.locator;

import com.windowtester.runtime.IUIContext;

/**
 * A locator identifying a widget that may or may not currently exist
 * such as a table item, menu, menu item, text field, etc.
 * Instances are responsible for locating widget(s) that they identify.
 */
public interface IWidgetLocator
	extends ILocator, IWidgetMatcher
{
	/**
	 * Find the widgets identified by the receiver.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return the widgets (e.g. SWT Component or Swing JComponent) identified by the receiver
	 * 			(not <code>null</code>, contains no <code>null</code>s but may be empty)
	 */
	IWidgetLocator[] findAll(IUIContext ui);
}
