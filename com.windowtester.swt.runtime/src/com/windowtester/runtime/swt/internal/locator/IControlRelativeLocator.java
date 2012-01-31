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
package com.windowtester.runtime.swt.internal.locator;

import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * Marker for locators that are relative to a control.
 *
 */
public interface IControlRelativeLocator {

	/**
	 * Get the locator that identifies the control containing this virtual item.
	 */
	SWTWidgetLocator getControlLocator();
	
}
