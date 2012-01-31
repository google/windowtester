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
package com.windowtester.swt.runtime.internal.macosx;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {

	/**
	 * Return the value (as an Object) of the given field from the given object.
	 * @return the {@link Object} value of field with given name.
	 */
	public static Object getFieldObject(final Object object, final String name) {
		Class<?> refClass = getRefClass(object);
		Object refObject = getRefObject(object);
		Field field = getFieldByName(refClass, name);
		if (field == null) {
			throw new IllegalArgumentException("Unable to find '" + name + "' in " + refClass);
		}
		Throwable thrown = null;
		try {
			return field.get(refObject);
		} catch (IllegalAccessException ex) {
			thrown = ex;
		} catch (IllegalArgumentException ex) {
			thrown = ex;
		} catch (NullPointerException ex) {
			thrown = ex;
		} catch (ExceptionInInitializerError ex) {
			thrown = ex;
		}
		// DEBUG
		thrown.printStackTrace();
		throw new RuntimeException("ERROR in getFieldObject() " + thrown.getMessage());
	}

	/**
	 * Return the value (as an int) of the given field from the given object.
	 * @return the <code>int</code> value of field with given name.
	 */
	public static int getFieldInt(Object object, String name) {
		return ((Integer) getFieldObject(object, name)).intValue();
	}

	/**
	 * @return the <code>long</code> value of field with given name.
	 */
	public static long getFieldLong(Object object, String name) {
		return ((Long) getFieldObject(object, name)).longValue();
	}

	/**
	 * Return the field with the given name in the given class.
	 * TODO Verify search order corresponds to Java spec.
	 * @return the {@link Field} of given class with given name or <code>null</code> if no such {@link Field} found.
	 */
	public static Field getFieldByName(Class<?> theClass, String name) {
		Class<?> clazz = theClass;
		// check fields of given class and its super classes
		while (clazz != null) {
			// check all declared field
			Field[] declaredFields = clazz.getDeclaredFields();
			for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];
				if (field.getName().equals(name)) {
					field.setAccessible(true);
					return field;
				}
			}
			// check interfaces
			Class<?>[] interfaceClasses = clazz.getInterfaces();
			for (int i = 0; i < interfaceClasses.length; i++) {
				Class<?> interfaceClass = interfaceClasses[i];
				Field field = getFieldByName(interfaceClass, name);
				if (field != null) {
					return field;
				}
			}
			// check superclass
			clazz = clazz.getSuperclass();
		}
		return null; // not found
	}

	/**
	 * @return the {@link Class} of given {@link Object} or casted object, if it is {@link Class} itself.
	 */
	private static Class<?> getRefClass(Object object) {
		return object instanceof Class<?> ? (Class<?>) object : object.getClass();
	}

	/**
	 * @return the {@link Object} that should be used as argument for {@link Field#get(Object)} and
	 *         {@link Method#invoke(Object, Object[])}.
	 */
	private static Object getRefObject(Object object) {
		return object instanceof Class<?> ? null : object;
	}
}
