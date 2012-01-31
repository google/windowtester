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

/**
 * A matcher that matches objects based on their class (by name).
 */
public class ClassMatcher extends SafeMatcher {

	private final Class _class;

	public ClassMatcher(Class cls) {
		assertNotNull(cls);
		_class = cls;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.matcher.SafeMatcher#matchesSafely(java.lang.Object)
	 */
	public boolean matchesSafely(Object toTest) {
		return toTest.getClass().equals(_class);
	}

}
