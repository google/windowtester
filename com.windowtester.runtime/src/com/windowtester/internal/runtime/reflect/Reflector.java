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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Simple method reflection helper.
 */
public class Reflector {

	
	public static Reflector forObject(Object o) {		   
		return new Reflector(o);
	 }

	
	private final Object object;

	private Reflector(Object object) {
		this.object = object;
	}

	public boolean supports(String methodName, Class[] argTypes) {
		return getMethod(methodName, argTypes) != null;
	}
	
	public boolean supports(String methodName) {
		return supports(methodName, null);
	}

	private Method getMethod(String methodName, Class[] argTypes) {
		Method method = getPublicMethod(methodName, argTypes);
		if (method != null)
			return method;
		return getDeclaredMethod(methodName, argTypes);
	}

	private Method getPublicMethod(String methodName, Class[] argTypes) {
		if (object == null)
			return null;
		try {
			return object.getClass().getMethod(methodName, argTypes);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return null;
	}

	private Method getDeclaredMethod(String methodName, Class[] argTypes) {
		if (object == null)
			return null;
		try {
			return object.getClass().getDeclaredMethod(methodName, null);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return null;
	}
	
	
	public Object invoke(String methodName){
		return invoke(methodName, null, null);
	}
	
	public Object invoke(String methodName, Class[] argTypes, Object [] args){
		if (object == null)
			return null;
		try {
			Method method = getMethod(methodName, argTypes);
			if (method == null)
				return null;
			method.setAccessible(true);
			return method.invoke(object, args);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}
	
	
}
