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

import com.windowtester.runtime.internal.IMatcher;


/**
 * Inspired by JMock's TypeSafeMatcher.
 */
public abstract class SafeMatcher implements IMatcher {

	/**
	 * Method made final to prevent accidental override. If you need to override this, implement the IMatcher interface directly.
	 * 
	 * @see com.windowtester.runtime.internal.IMatcher#matches(java.lang.Object)
	 */
	public final boolean matches(Object toTest) {
		if (toTest == null)
			return false;
		return matchesSafely(toTest);
	}

	/**
	 * Subclasses should implement this. The item is guaranteed not to be <code>null</code>.
	 * @param toTest the item to test
	 * @return <code>true</code> if item matches, <code>false</code> otherwise.
	 */
	public abstract boolean matchesSafely(Object toTest);
	
	
	protected static void assertNotNull(Object arg) throws IllegalArgumentException {
		if (arg == null)
			throw new IllegalArgumentException("Argument cannot be null");
	}

}