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
package com.windowtester.internal.runtime.util;

import java.text.NumberFormat;
import java.util.Arrays;

/**
 * String utilities.
 */
public class StringUtils {
	
    public static String NEW_LINE = System.getProperty("line.separator", "\n");

	/** 
	 *  Cloned from {@link Arrays#toString(Object[])} which is new
	 *  for Java 1.5.
	 */
	public static String toString(Object[] a) {
		if (a == null)
			return "null";
		if (a.length == 0)
			return "[]";

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < a.length; i++) {
			if (i == 0)
				buf.append('[');
			else
				buf.append(", ");

			buf.append(String.valueOf(a[i]));
		}

		buf.append("]");
		return buf.toString();
	}
	
	/**
	 * Return the count and the quantity label as a properly pluralized string.
	 * I.e. (5, "item") ==> "5 items".
	 * <p/>
	 * 
	 */
	public static String pluralize(int count, String single) {
		if (count == 1) {
			return NumberFormat.getNumberInstance().format(count) + " " + single;
		} else{
			return NumberFormat.getNumberInstance().format(count) + " " + single + "s";
		}
	}

    /**
     * Trim the specified menu item text by removing '&' and truncating the accelerator
     * 
     * @param actual the original menu item text (e.g. "Ne&w\tCtrl+N")
     * @return the trimmed menu item text
     */
    public static String trimMenuText(String actual) {
    	if (actual == null)
    		return null;
		String trimmed = actual;
		int index = trimmed.indexOf('\t');
		if (index != -1)
			trimmed = trimmed.substring(0, index);
		index = trimmed.indexOf('&');
		if (index != -1)
			trimmed = trimmed.substring(0, index) + trimmed.substring(index + 1);
		return trimmed;
	}
	
	
}
