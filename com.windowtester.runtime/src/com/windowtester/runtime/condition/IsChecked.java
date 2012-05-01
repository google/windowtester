/*******************************************************************************
 *  Copyright (c) 2012 Frederic Gurr
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Frederic Gurr - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.condition;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.ILocator;

/**
 * A specialized {@link ILocator} providing a convenience method to determine if the
 * underlying object is checked. See {@link IsCheckedCondition} for typical usage.
 */
public interface IsChecked extends ILocator {

	/**
	 * Resolve the locator to a single object and determine if that object is checked.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return <code>true</code> if the object is checked, else false
	 */
	public boolean isChecked(IUIContext ui) throws WidgetSearchException;
}