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
package com.windowtester.runtime.internal.matcher;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.windowtester.runtime.internal.IMatcher;
import com.windowtester.runtime.internal.util.PropertyUtil;

/**
 * A matcher that tests whether a JavaBean-style property on given object satisfies the 
 * provided matching criteria. 
 */
public class PropertyMatcher extends SafeMatcher {

	private static final Object[] NO_ARGS = new Object[0];

	private final String _propertyName;
	private final IMatcher _propertyMatcher;

	public PropertyMatcher(String propertyName, IMatcher propertyMatcher) {
		_propertyName     = propertyName;
		_propertyMatcher  = propertyMatcher;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.matcher.SafeMatcher#matchesSafely(java.lang.Object)
	 */
	public boolean matchesSafely(Object toTest) {
		try {
			Method readMethod = PropertyUtil.getReadMethod(_propertyName, toTest);
			return readMethod != null
					&& _propertyMatcher.matches(readMethod.invoke(toTest, NO_ARGS));

		} catch (IntrospectionException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return false;
	}

	
	public static PropertyMatcher hasProperty(String propertyName, IMatcher propMatcher) {
		return new PropertyMatcher(propertyName, propMatcher);
	}
	

}