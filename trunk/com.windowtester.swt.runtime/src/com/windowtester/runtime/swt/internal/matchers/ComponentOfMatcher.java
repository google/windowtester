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

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * A matcher that tests whether a widget is a component in (or indirect child of) a widget matched by
 * the given matcher.
 */
public class ComponentOfMatcher implements ISWTWidgetMatcher {

	private final ISWTWidgetMatcher parentMatcher;
//	private SWTHierarchyHelper helper;
	
	
	public ComponentOfMatcher(ISWTWidgetMatcher parentMatcher) {
		this.parentMatcher = parentMatcher;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> ref) {
		ISWTWidgetReference<?> parent = ref.getParent();
		while (parent != null) {
			//System.out.println("testing parent: " + UIProxy.getToString(parent) + " against " + parentMatcher);
			if (parentMatcher.matches(parent))
				return true;
			//System.out.println("-> false");
			parent = parent.getParent();
		}
		return false;
	}
	
//	public boolean matchesWidget(Widget widget) {
//		
//		Widget parent = getParent(widget);
//		while (parent != null) {
//			//System.out.println("testing parent: " + UIProxy.getToString(parent) + " against " + parentMatcher);
//			if (parentMatcher.matches(parent))
//				return true;
//			//System.out.println("-> false");
//			parent = getParent(parent);
//		}
//		return false;
//	}


//	private Widget getParent(Widget widget) {
//		return getHelper().getParent(widget);
//	}
//
//	private SWTHierarchyHelper getHelper() {
//		if (helper == null)
//			helper = new SWTHierarchyHelper();
//		return helper;
//	}

//	public int getIndex(Widget widget, IWidgetMatcher matcher) {
//		Widget root = findParentRoot(widget);
//		if (root == null)
//			return WidgetLocator.UNASSIGNED;
//		Widget[] matches = findAllChildMatches(root, matcher);
//		if (matches.length == 1)
//			return WidgetLocator.UNASSIGNED;
//		for (int i = 0; i < matches.length; i++) {
//			if (matches[i] == widget)
//				return i;
//		}
//		return WidgetLocator.UNASSIGNED;
//	}

//	protected Widget[] findAllChildMatches(Widget root, IWidgetMatcher matcher) {
//		return new SWTWidgetFinder(Context.GLOBAL.getUI()).findAllInScope(matcher, root);
//	}

//	private Widget findParentRoot(Widget widget) {
//		Widget parent = getParent(widget);
//		while (parent != null) {
//			//System.out.println("testing parent: " + parent + " against " + _parentMatcher);
//			if (parentMatcher.matches(parent))
//				return parent;
//			parent = getParent(parent);
//		}
//		return null;
//	}
	
}
