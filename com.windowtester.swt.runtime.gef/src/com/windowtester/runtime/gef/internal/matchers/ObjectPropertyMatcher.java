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
package com.windowtester.runtime.gef.internal.matchers;

import com.windowtester.runtime.internal.IMatcher;
import com.windowtester.runtime.internal.matcher.EqualityMatcher;
import com.windowtester.runtime.internal.matcher.PropertyMatcher;

public class ObjectPropertyMatcher /* implements IModelObjectMatcher */ {

	private PropertyMatcher _matcher;

	public ObjectPropertyMatcher(String property, Object value) {
		this(property, new EqualityMatcher(value));
	}
	
	public ObjectPropertyMatcher(String property, IMatcher valueMatcher) {
		_matcher = new PropertyMatcher(property, valueMatcher);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object toTest) {
		return _matcher.matches(toTest);
	}

}
