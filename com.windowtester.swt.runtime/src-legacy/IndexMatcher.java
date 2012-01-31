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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.swt.WidgetLocatorService;

/**
 * This class is deprecated pending further investigation.
 * 
 * Use {@link SWTHierarchyMatcher} instead.
 * @author Phil Quitslund
 * @deprecated
 *
 */
public class IndexMatcher extends SWTParentMatcher {

	public static IndexMatcher create(IWidgetMatcher parentCriteria, int index) {
		return new IndexMatcher(parentCriteria, index);
	}
		
//	public static IndexMatcher2 create(IWidgetMatcher childCriteria, int index, IWidgetMatcher parentCriteria) {
//		return new IndexMatcher2(childCriteria, index, parentCriteria);
//	}
	
	private final int _expectedIndex;
	private Object _parent;

	public IndexMatcher(IWidgetMatcher parentCriteria, int index) {
		super(parentCriteria);
		_expectedIndex = index;
	}
	
	public boolean matches(Object widget) {
		if (!super.matches(widget))
			return false;
		return indexMatches(widget);
	}

	public Object getParent(Object widget) {
		//cache the calculated parent so we don't need to fetch it again
		_parent = super.getParent(widget);
		return _parent;
	}
	
	private boolean indexMatches(Object child) {
		//TODO: handle casts elsewhere...
		int actualIndex = new WidgetLocatorService().getIndex((Widget)child, (Widget)_parent);
		return actualIndex == _expectedIndex;
	}
	
	List getMatchesInParentsChildren(Object widget) {
		
		List children = new WidgetLocatorService().getChildren((Widget)_parent);
		List matches = new ArrayList();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Object child= iter.next();
			if (super.matches(child))
				matches.add(child);
		}
		return matches;
		
	}
	
	
}
