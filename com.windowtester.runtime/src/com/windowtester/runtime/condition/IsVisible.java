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
package com.windowtester.runtime.condition;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.ILocator;

/**
 * A specialized {@link ILocator} providing a convenience method to determine if the
 * underlying object is visible. See {@link IsVisibleCondition} for typical usage.
 */
public interface IsVisible extends ILocator
{
	/**
	 * Resolve the locator to a single object and determine if that object is visible.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return <code>true</code> if the object is visible, else false
	 */
	boolean isVisible(IUIContext ui) throws WidgetSearchException;
}
