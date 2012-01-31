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
package com.windowtester.recorder.event.user;

import org.eclipse.swt.graphics.Point;

import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.runtime.locator.ILocator;

/**
 * A widget description is used in widget inspections.
 */
public interface IWidgetDescription {

	ILocator getLocator();
	PropertySet getProperties();
	Point getHoverPoint();
	boolean isSame(IWidgetDescription event);

	
	/**
	 * Return a string that can be used to label this description in the UI.
	 * (Can be null.)
	 */
	String getDescriptionLabel();
}
