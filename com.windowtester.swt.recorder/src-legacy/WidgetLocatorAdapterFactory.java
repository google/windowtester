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
package com.windowtester.swt.event.model;

import com.windowtester.swt.WidgetLocator;

/**
 * A factory that adapts generic widget locators to SWT widget locators.
 * This is used for backwards-compatability, and should eventually go away.
 *
 */
public class WidgetLocatorAdapterFactory {

	public static WidgetLocator adapt(com.windowtester.runtime.WidgetLocator loc) {
		
		WidgetLocator target = extractTargetLocator(loc);
		while(target != null) {
			WidgetLocator parent = extractTargetLocator(loc.getParentInfo());
			target.setParentInfo(parent);
			target = parent;
		}
		return target;
	}

	private static WidgetLocator extractTargetLocator(com.windowtester.runtime.WidgetLocator loc) {
		if (loc == null)
			return null;
		
		Class targetClass = loc.getTargetClass();
		String nameOrLabel = loc.getNameOrLabel();
		int index = loc.getIndex();
		return new WidgetLocator(targetClass, nameOrLabel, index);
	}

}
