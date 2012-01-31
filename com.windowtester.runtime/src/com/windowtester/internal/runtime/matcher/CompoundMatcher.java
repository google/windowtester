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
package com.windowtester.internal.runtime.matcher;

import com.windowtester.runtime.locator.IWidgetMatcher;

/**
 * Compounds/composes/aggregates Matchers. 
 */
public class CompoundMatcher implements IWidgetMatcher {

	private final IWidgetMatcher _componentMatcher1;
	private final IWidgetMatcher _componentMatcher2;
	
	
	public static IWidgetMatcher create(IWidgetMatcher m1, IWidgetMatcher m2) {
		return new CompoundMatcher(m1, m2);
	}
	
	public CompoundMatcher(IWidgetMatcher m1, IWidgetMatcher m2) {
		_componentMatcher1 = m1;
		_componentMatcher2 = m2;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime2.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return _componentMatcher1.matches(widget) && _componentMatcher2.matches(widget);
	}



	
	
}
