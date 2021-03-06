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
package com.windowtester.runtime.swt.internal.matcher;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.Context;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;
import com.windowtester.runtime.swt.internal.finder.legacy.SWTWidgetFinder;
import com.windowtester.runtime.swt.internal.finder.matchers.IComponentIndexer;

/**
 * A matcher that tests whether a widget is contained in a wigdet matched by
 * the given locator.
 * @deprecated
 */
public class ContainedInWidgetMatcher implements IWidgetMatcher, IComponentIndexer {

	private final IWidgetMatcher parentMatcher;
	private SWTHierarchyHelper helper;
	
	
	public ContainedInWidgetMatcher(IWidgetMatcher parentMatcher) {
		this.parentMatcher = parentMatcher;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		if (!(widget instanceof Widget))
			return false;
		return matches((Widget)widget);
	}
	
	public boolean matches(Widget widget) {
		
		Widget parent = getParent(widget);
		while (parent != null) {
			//System.out.println("testing parent: " + UIProxy.getToString(parent) + " against " + parentMatcher);
			if (parentMatcher.matches(parent))
				return true;
			//System.out.println("-> false");
			parent = getParent(parent);
		}
		return false;
	}


	private Widget getParent(Widget widget) {
		return getHelper().getParent(widget);
	}

	private SWTHierarchyHelper getHelper() {
		if (helper == null)
			helper = new SWTHierarchyHelper();
		return helper;
	}

	public int getIndex(Widget widget, IWidgetMatcher matcher) {
		Widget root = findParentRoot(widget);
		if (root == null)
			return WidgetLocator.UNASSIGNED;
		Widget[] matches = findAllChildMatches(root, matcher);
		if (matches.length == 1)
			return WidgetLocator.UNASSIGNED;
		for (int i = 0; i < matches.length; i++) {
			if (matches[i] == widget)
				return i;
		}
		return WidgetLocator.UNASSIGNED;
	}

	protected Widget[] findAllChildMatches(Widget root, IWidgetMatcher matcher) {
		return new SWTWidgetFinder(Context.GLOBAL.getUI()).findAllInScope(matcher, root);
	}

	private Widget findParentRoot(Widget widget) {
		Widget parent = getParent(widget);
		while (parent != null) {
			//System.out.println("testing parent: " + parent + " against " + _parentMatcher);
			if (parentMatcher.matches(parent))
				return parent;
			parent = getParent(parent);
		}
		return null;
	}
	
}
