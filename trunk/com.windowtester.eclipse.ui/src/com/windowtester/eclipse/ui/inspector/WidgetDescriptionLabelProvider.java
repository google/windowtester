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

import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.user.IWidgetDescription;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * Helper for extracting top-level description labels for presentation in the
 * UI.
 */
public class WidgetDescriptionLabelProvider {
	
	public static String getDescription(IWidgetDescription widget) {
		
		String description = widget.getDescriptionLabel();
		if (description != null)
			return description;
		
		ILocator locator = widget.getLocator();
		if (locator instanceof IdentifierAdapter)
			locator = ((IdentifierAdapter)locator).getLocator();
		if (locator == null)
			return "<no locator>"; //TODO: flesh this case out
		
		if (locator instanceof SWTWidgetLocator) {
			SWTWidgetLocator swtWidget = (SWTWidgetLocator)locator;
			return getSimpleClassName(swtWidget.getTargetClassName());
		}
		//TODO: lame use of reflection to avoid adding dep on optional GEF jar:
		if (contains(locator.getClass().getName(), ".gef."))
			return "Figure";
		
		//hack...
		String className = getSimpleClassName(locator.getClass().getName());
		String simpleName = className.replaceFirst("Locator.*", "");
		if (simpleName != null && simpleName.trim().length() > 0)
			return simpleName;
		
		return locator.toString();
	}
	
	
	private static boolean contains(String string, String substring) {
		return string.indexOf(substring) > -1;
	}

	private static String getSimpleClassName(String className) {
		/*
		 * To make this more sophisticated -- e.g., handle inner classes, adapt:
		 * Java5.0 {@link Class#getSimpleName()}.
		 */
		return className.substring(className.lastIndexOf(".")+1); // strip the package name
	}
}
