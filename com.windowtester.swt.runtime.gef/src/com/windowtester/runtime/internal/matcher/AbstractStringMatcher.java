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
 * A base class for matchers that match strings.
 */
public abstract class AbstractStringMatcher extends SafeMatcher {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.matcher.SafeMatcher#matchesSafely(java.lang.Object)
	 */
	public final boolean matchesSafely(Object toTest) {
		if (!(toTest instanceof String))
			return false;
		return stringMatches((String) toTest);
	}

	/**
	 * Test this string for a match.
	 */
	protected abstract boolean stringMatches(String toTest);

}
