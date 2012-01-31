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
 *
 * @author Phil Quitslund
 * @deprecated
 *
 */
public class IndexMatcher2 implements IWidgetMatcher {

	
//	public static IndexMatcher2 create(IWidgetMatcher parentCriteria, int index) {
//		return new IndexMatcher2(parentCriteria, index);
//	}
	
	
	//TODO: refactor to use a compound matcher!
	//			--> new CompoundMatcher(childCrit, new CompoundMatcher(parentCrit, indexCheckingCrit));
	//			--> new CompoundMatcher(new HierarchyMatcher(..,..), indexCheckingCrit);
	
	private final int _expectedIndex;
	//private Object _parent;
	private final IWidgetMatcher _childCriteria;
	private final IWidgetMatcher _parentCriteria;

	public IndexMatcher2(IWidgetMatcher childCriteria, int index, IWidgetMatcher parentCriteria) {
		_childCriteria = childCriteria;
		_expectedIndex = index;
		_parentCriteria = parentCriteria;
	}
	
	public boolean matches(Object widget) {
		if (!_childCriteria.matches(widget))
			return false;
		return _parentCriteria.matches(getParent(widget)) && indexMatches(widget);
	}

	public Object getParent(Object widget) {
		return new WidgetLocatorService().getParent((Widget)widget);
	}
	
	private boolean indexMatches(Object widget) {
		List matches = getMatchesInParentsChildren(widget);
		int actualIndex = matches.indexOf(widget);
		
		//TODO: handle casts elsewhere...
		//int actualIndex = new WidgetLocatorService().getIndex((Widget)child, (Widget)_parent);
		return actualIndex == _expectedIndex;
	}
	
	public List getMatchesInParentsChildren(Object widget) {	
		Widget parent = (Widget)getParent(widget);
		List children = new WidgetLocatorService().getChildren(parent);
		List matches = new ArrayList();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Object child= iter.next();
			if (_childCriteria.matches(child))
				matches.add(child);
		}
		return matches;
	}
	
	
}
