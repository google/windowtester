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
package com.windowtester.runtime.swt.internal.matchers;

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.MenuItemReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * A matcher that matches menu items based on their path descriptions.
 * <p>
 * For example, suppose a menu consists of a parent node "File" and a 
 * child node "Save", the child would be matched by a matcher defined
 * this way:
 * <pre>
 * 	new MenuItemByPathMatcher("File/Save");
 * </pre>
 */
public class MenuItemByPathMatcher extends WidgetMatcher {

	/** The path to match */
	private final String pathString;

	/**
	 * Create an instance.
	 * @param pathString the path to match (e.g., "parent/child") 
	 */
	public MenuItemByPathMatcher(String pathString) {
		this.pathString = pathString;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> ref) {
		if (!(ref instanceof MenuItemReference))
			return false;
		
		MenuItemReference menuItem = (MenuItemReference)ref;
		String actualPathString = menuItem.getPathString();

		if (actualPathString == null)
			return pathString == null;
//		System.out.println("matching: " + actualPathString + " against expected: " + pathString);
		boolean matches = StringComparator.matches(actualPathString, pathString);
//		if (matches)
//			System.out.println("****** MATCHES");
		return matches;
	}

}
