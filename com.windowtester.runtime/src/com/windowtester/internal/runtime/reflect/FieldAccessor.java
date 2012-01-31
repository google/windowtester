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
package com.windowtester.internal.runtime.reflect;

import java.lang.reflect.Field;

/**
 * Field access helper.
 */
public class FieldAccessor {
	private Class cls;
	private Object object;
	private String fieldName;
	
	public static FieldAccessor forObject(Object o) {
		FieldAccessor accessor = new FieldAccessor();
		accessor.object = o;
		return accessor;
	}
	
	public static FieldAccessor forClass(Class cls) {
		FieldAccessor accessor = new FieldAccessor();
		accessor.cls = cls;
		return accessor;
	}
	
	public static FieldAccessor forField(String fieldName) {
		FieldAccessor accessor = new FieldAccessor();
		accessor.fieldName = fieldName;
		return accessor;
	}
	
	public FieldAccessor inClass(Class cls) {
		this.cls = cls;
		return this;
	}
	
	
	public Object access(Object object) {
		this.object = object;
		return access(this.fieldName);
	}
	
	public Object access(String fieldName) {
		try {
			Field field = cls.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}
}