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

/**
 * Widget Matchers are used to test whether a Widget matches 
 * some desired criteria.
 */
public interface IWidgetMatcher<T>
{
	 
	/**
	 * Check whether the given Widget satisfies the specified criteria.
	 * @param widget the widget to test
	 * @return <code>true</code> if the widget matches, 
	 * 		<code>false</code> otherwise
	 */
	boolean matches(T widget);
	
}
