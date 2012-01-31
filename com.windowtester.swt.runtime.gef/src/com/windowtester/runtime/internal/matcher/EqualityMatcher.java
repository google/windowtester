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

import java.lang.reflect.Array;

import com.windowtester.runtime.internal.IMatcher;

/**
 * Matches based on whether the value is equal to another value, as tested by the
 * {@link java.lang.Object#equals} method.
 * <p>
 * Note that the argument object may be <code>null</code>, in whcih case teh matcher will return <code>true</code>
 * in case the object to test is also  <code>null</code>.
 * 
 */
public class EqualityMatcher implements IMatcher {

	public static IMatcher create(Object value) {
		return new EqualityMatcher(value);
	}
	
	private final Object _object;

	public EqualityMatcher(Object object) {
		_object = object;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object toTest) {
		return areEqual(_object, toTest);
	}

	private static boolean areEqual(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return o1 == null && o2 == null;
		} else if (isArray(o1)) {
			return isArray(o2) && areArraysEqual(o1, o2);
		} else {
			return o1.equals(o2);
		}
	}

	private static boolean areArraysEqual(Object o1, Object o2) {
		return areArrayLengthsEqual(o1, o2) && areArrayElementsEqual(o1, o2);
	}

	private static boolean areArrayLengthsEqual(Object o1, Object o2) {
		return Array.getLength(o1) == Array.getLength(o2);
	}

	private static boolean areArrayElementsEqual(Object o1, Object o2) {
		for (int i = 0; i < Array.getLength(o1); i++) {
			if (!areEqual(Array.get(o1, i), Array.get(o2, i)))
				return false;
		}
		return true;
	}

	private static boolean isArray(Object o) {
		return o.getClass().isArray();
	}

}
