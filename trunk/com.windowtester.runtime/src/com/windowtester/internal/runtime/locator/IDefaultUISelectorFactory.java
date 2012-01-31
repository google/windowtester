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

/**
 * A generator for default widget selectors.
 */
public interface IDefaultUISelectorFactory {

	/**
	 * Create a widget selector for this widget if appropriate, else return null.
	 * @param widget the widget to adapt to a selector.
	 * @return an appropriate widget reference object.
	 */
	IUISelector create(Object widget);
	
	
}
