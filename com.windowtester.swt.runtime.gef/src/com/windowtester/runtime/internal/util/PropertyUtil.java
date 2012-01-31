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
package com.windowtester.runtime.internal.util;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * (Based on similar class from JMock)
 * 
 * Utility class for accessing properties on JavaBean objects.
 * 
 * See http://java.sun.com/products/javabeans/docs/index.html for
 * more information on JavaBeans.
 */
public class PropertyUtil {

	/**
	 * Returns the description of the property with the provided
	 * name on the provided object's interface.
	 * @return the description of the property, or null if the
	 * property does not exist.
	 * 
	 * @throws IntrospectionException if an error occured using
	 * the JavaBean Introspector class to query the properties
	 * of the provided class. 
	 */
	public static PropertyDescriptor getPropertyDescriptor(String propertyName, Object fromObj) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(fromObj.getClass());
		PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
		
		for(int i=0; i < properties.length; i++) {
			if(properties[i].getName().equals(propertyName)) {
				return properties[i];
			}
		}
		return null;
	}
	
	public static Method getReadMethod(String propertyName, Object argument) throws IntrospectionException {
		PropertyDescriptor property = getPropertyDescriptor(propertyName, argument);
		return property == null ? null : property.getReadMethod();
	}
	
}
