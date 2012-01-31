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
package com.windowtester.runtime.swt.internal.matchers;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;


/**
 * Matches direct children.  For containment tests, see {@link ComponentOfMatcher} or {@link ContainedInMatcher}.
 */
public class ChildOfMatcher extends WidgetMatcher {

	public static final int UNSPECIFIED_INDEX = -1;
	private final ISWTWidgetMatcher targetMatcher;
	private final int index;
	private final ISWTWidgetMatcher parentMatcher;

	public ChildOfMatcher(ISWTWidgetMatcher target, ISWTWidgetMatcher parent) {
		this(target, UNSPECIFIED_INDEX, parent);
	}

	public ChildOfMatcher(ISWTWidgetMatcher target, int index, ISWTWidgetMatcher parent) {
		this.index = index;
		this.targetMatcher = target;
		this.parentMatcher = parent;
	}

	
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> widget) {
				
		//is this log-worthy?
		if (widget == null)
			return false;
				
		/* 
		 * various fast-fail optimizations
		 */
		
		//check target matcher
		if (!targetMatcher.matches(widget))
			return false; //fast fail
		
		//if target matches, turn to parent
		
		//first, short-circuit if there is no parent matcher
//		if (parentMatcher == null)
//			return true;
		
		//next, check parent matcher

//		WidgetLocatorService infoService = new WidgetLocatorService();
//		Widget parent   = infoService.getParent(widget);

		ISWTWidgetReference<?>parent = widget.getParent();
		
		
		if (parent == null)
			return false;    //if there is no parent, but there is a matcher, return false
		
		if (!parentMatcher.matches(parent))
			return false;   //fail if parent does not match
		
		//lastly, check index
		//return testIndex(widget, infoService, parent);
		return testIndex(widget, parent);
	}

//	private boolean testIndex(Widget widget, WidgetLocatorService infoService, Widget parent) {
//		//NOTE: some matchers override the infoService indexer...
//		if (_parentMatcher instanceof IComponentIndexer) {
//			//in case no index is assigned, any index match will do
//			//this handles the case where a user wants ALL children of a widget
//			
//			//TODO: this logic might apply to the general case as well...
//			if (_index == DEFAULT_INDEX)
//				return true;
//			return ((IComponentIndexer)_parentMatcher).getIndex(widget, _matcher) == _index;
//		}
//		// check if there is an index , only then do a match
//		if (_index == DEFAULT_INDEX)
//			return true;
//		return infoService.getIndex(widget, parent) == _index;
//	}
	
	
	private boolean testIndex(ISWTWidgetReference<?> widget,
			ISWTWidgetReference<?> parent) {
		// check if there is an index , only then do a match
		if (index == UNSPECIFIED_INDEX)
			return true;
		return getIndex(widget, parent) == index;
	}

	private int getIndex(ISWTWidgetReference<?> widget, ISWTWidgetReference<?> parent) {
		ISWTWidgetReference<?>[] children = parent.getChildren();
		int index = -1;   //the index of our target widget
		//only child case...
		if (children.length == 1)
			return index;
		List<ISWTWidgetReference<?>> matchedChildren = pruneMatches(children);
		for (ISWTWidgetReference<?> match : matchedChildren) {
			++index;
			Object w = match.getWidget();
			if (w == widget.getWidget())
				return index;
		}
		return -1;
	}

	private List<ISWTWidgetReference<?>> pruneMatches(
			ISWTWidgetReference<?>[] children) {
		List<ISWTWidgetReference<?>> matches = new ArrayList<ISWTWidgetReference<?>>();
		for (int i = 0; i < children.length; i++) {
			ISWTWidgetReference<?> child = children[i];
			if (targetMatcher.matches(child))
				matches.add(child);
		}
		return matches;
	}

	
	@Override
	public String toString() {
		return targetMatcher + " in " + parentMatcher;
	}



}
