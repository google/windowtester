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
package com.windowtester.runtime.swt.internal.selection;

import com.windowtester.internal.runtime.ISelectionTarget;
import com.windowtester.internal.runtime.selector.ClickHelper;
import com.windowtester.runtime.ClickDescription;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * A helper to parse selection targets.
 *
 */
public class SelectionTarget implements ISelectionTarget {

	public static ISelectionTarget parse(ILocator locator) {
		IClickDescription clickDescription = ClickDescription.create(1, locator, WT.BUTTON1); //default values can be overrriden later
		IWidgetLocator widgetLocator       = ClickHelper.getWidgetLocator(locator);
		return new SelectionTarget(clickDescription, widgetLocator);
	}
	
	private final IClickDescription _click;
	private final IWidgetLocator  _locator;
	
	
	public SelectionTarget(IClickDescription click, IWidgetLocator locator) {
		_click = click;
		_locator = locator;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.selection.ISelectionTarget#getClickDescription()
	 */
	public IClickDescription getClickDescription() {
		return _click;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.selection.ISelectionTarget#getWidgetLocator()
	 */
	public IWidgetLocator getWidgetLocator() {
		return _locator;
	}

}
