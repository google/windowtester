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
package com.windowtester.eclipse.ui.inspector;

import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.abbot.matcher.NameOrLabelMatcher;

public class LocatorString {

	
	private static final int MAX_DESC_LENGTH = 12;

	public static String forDisplay(ILocator locator) {
		return simpleName(locator) + description(locator);
	}
	
	public static String forDisplayShort(ILocator locator) {
		String description = description(locator);
		if (description.length() > MAX_DESC_LENGTH)
			description = " \"...\"";
		return simpleName(locator) + description;
	}
	
	
	private static String description(ILocator locator) {
		String d = "";
		
		if (locator instanceof WidgetLocator) {
			d = ((WidgetLocator)locator).getNameOrLabel();
		}
		if (d== null || d.length()==0)
			return "";
		
		return " \"" + d +"\"";  
	}


	private static String simpleName(ILocator locator) {
		return getSimpleClassName(locator.getClass());
	}


	protected static String getSimpleClassName(Class cls) {
		/*
		 * To make this more sophisticated -- e.g., handle inner classes, adapt:
		 * Java5.0 {@link Class#getSimpleName()}.
		 */
		String simpleName = cls.getName();
		return simpleName.substring(simpleName.lastIndexOf(".") + 1); // strip name
	}
	
	protected static String escapeAmpersands(ILocator locator) {
		String string = locator.toString();
		return NameOrLabelMatcher.trimMenuText(string);	
	}



	
}
