/*******************************************************************************
 *  Copyright (c) Frederic Gurr
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
 * A specialized {@link ILocator} providing a convenience method to extract the minimum value
 * from the underlying object. See {@link HasMinimumCondition} for typical usage.
 */
public interface HasMinimum
{
	/**
	 * Resolve the locator to a single object and answer the minimum value associated with it.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return the minimum value associated with that object
	 */
	int getMinimum(IUIContext ui) throws WidgetSearchException;
}
