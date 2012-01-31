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
package com.windowtester.runtime.gef.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.windowtester.runtime.util.StringComparator;

public class TextHelper {
	
	public static String getText(Object widget) {
		return getStringMethodValue(widget, "getText");
	}
	
	public static String getStringMethodValue(Object widget, String methodName) {
		if (widget == null)
			return null;
		
		// Try to get the text property through the getText method
		Class figureClass = widget.getClass();
		try {
			Method m = figureClass.getMethod("getText", (Class[]) null);
			m.setAccessible(true);
			String sourceText = (String) m.invoke(widget, (Object[]) null);
			return sourceText;
		}
		// Not all figures have a getText method because not all figures
		// have text. Catch the reflection exceptions and do nothing.
		// Don't just catch Exception so any other unexpected exceptions
		// are actually thrown
		catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		
		return null;
	}
	
	public static String getStringFieldValue(Object widget, String fieldName) {
		if (widget == null) {
			return null;
		}
		Class figureClass = widget.getClass();
		try {
			Field field = figureClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			String uniqueId = (String) field.get(widget);
			return uniqueId;
		}
		// Not all figures have a getText method because not all figures
		// have text. Catch the reflection exceptions and do nothing.
		// Don't just catch Exception so any other unexpected exceptions
		// are actually thrown
		catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		
		return null;
	}
	
	public static boolean textMatches(Object widget, String textOrPattern) {		
		return StringComparator.matches(getText(widget),textOrPattern);
	}
	
}
