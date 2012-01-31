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
package com.windowtester.runtime.swt.internal.finder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.SWTHierarchy;

import com.windowtester.runtime.swt.internal.abbot.matcher.InstanceMatcher;
import com.windowtester.runtime.swt.internal.finder.legacy.WidgetFinder;
import com.windowtester.runtime.swt.internal.finder.legacy.WidgetFinder.MatchResult;

public class FinderHelper {

	
	///////////////////////////////////////////////////////////////////////////////////
	//
	// Internal helpers.
	//
	///////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get all of the Controls above this child in the widget hierarchy.
	 * @param child the child widget
	 * @return the child's parents
	 */
	public static List<Control> getParentControls(Widget child) {
		List<Control> controls = new ArrayList<Control>();
		collectControls(child, controls, new SWTHierarchy(child.getDisplay()));
		return controls;	
	}
	
	/**
	 * Test whether the given widget is somewhere below the given root in the widget 
	 * hierarchy.
	 */
	public static boolean isParentTo(Widget root, Widget possibleChild) {
		MatchResult result = new WidgetFinder().find(root, new InstanceMatcher(possibleChild), 0 /* no retries! */);
		return result.getType() == WidgetFinder.MATCH;
	}
	
	
	/*
	 * Collect parent controls.
	 */
	private static void collectControls(Widget widget, List<Control> controls, SWTHierarchy hierarchy) {
		do {
			widget = hierarchy.getParent(widget);
			if (widget instanceof Control)
				controls.add((Control) widget);
		} while (widget != null);
	}
	
}
