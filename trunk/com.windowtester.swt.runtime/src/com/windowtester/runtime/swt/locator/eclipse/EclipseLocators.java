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
package com.windowtester.runtime.swt.locator.eclipse;

import com.windowtester.runtime.swt.locator.SWTLocators;

/**
 * A factory for creating common Eclipse locators.
 * @see SWTLocators
 */
public class EclipseLocators {

	/**
	 * Create a locator for the view with the given name. 
	 * <p>
	 * This is a convenience method equivalent to {@link ViewLocator#forName(String)}.
	 */
	public static ViewLocator view(String viewName) {
		return ViewLocator.forName(viewName);
	}
	
	/**
	 * Create a locator for the view with the given id. 
	 * <p>
	 * This is a convenience method equivalent to {@link ViewLocator#forId(String)}.
	 */
	public static ViewLocator viewWithId(String viewId) {
		return ViewLocator.forId(viewId);
	}
	
	
	/**
	 * Create a locator for the perspective with the given name. 
	 * @since 3.8.1
	 */
	public static PerspectiveLocator perspective(String perspectiveName) {
		return PerspectiveLocator.forName(perspectiveName);
	}
	
	/**
	 * Create a new workbench locator.
	 * @since 3.7.1
	 */
	public static WorkbenchLocator workbench() {
		return new WorkbenchLocator();
	}
	
}
