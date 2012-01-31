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
package com.windowtester.runtime.swt.internal.finder.matchers;

import com.windowtester.runtime.locator.IWidgetMatcher;

/**
 * This class is deprecated pending further investigation.
 * 
 * Use {@link SWTHierarchyMatcher} instead.
 *
 * @deprecated
 *
 */
public class HierarchyMatcher implements IWidgetMatcher {

	public static IWidgetMatcher create(IWidgetMatcher child, IWidgetMatcher parent) {
		return new HierarchyMatcher(child, parent);
	}
	
	IWidgetMatcher _component;
	
	HierarchyMatcher(IWidgetMatcher child, IWidgetMatcher parent) {
		_component = new CompoundMatcher(child, new SWTParentMatcher(parent));
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime2.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return _component.matches(widget);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Debugging
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Hierarchy matcher (" + _component + ")";
    }
}
