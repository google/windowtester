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
 * underlying object is enabled. See {@link IsEnabledCondition} for typical usage.
 */
public interface IsEnabled extends ILocator
{
	/**
	 * Resolve the locator to a single object and determine if that object is enabled.
	 * 
	 * @param ui the UI context in which to find the widgets
	 * @return <code>true</code> if the object is enabled, else false
	 */
	boolean isEnabled(IUIContext ui) throws WidgetSearchException;
}
