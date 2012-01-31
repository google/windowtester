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
 * Internal methods for locators... in this case for recording MenuItemLocators
 */
public class LocatorPathUtil
{

	public static String stripAccelerators(String pathString) {
		if (pathString == null)
			return null;
		while (true) {
			int start = pathString.indexOf('\t');
			if (start == -1)
				break;
			int end = pathString.indexOf('/', start);
			if (end == -1)
				pathString = pathString.substring(0, start);
			else
				pathString = pathString.substring(0, start) + pathString.substring(end, pathString.length());
		}
		return pathString;
	}

	public static String stripAmpersands(String pathString) {
		if (pathString == null)
			return null;
		while (true) {
			int index = pathString.indexOf('&');
			if (index == -1)
				break;
			pathString = pathString.substring(0, index) + pathString.substring(index + 1, pathString.length());
		}
		return pathString;
	}

}
