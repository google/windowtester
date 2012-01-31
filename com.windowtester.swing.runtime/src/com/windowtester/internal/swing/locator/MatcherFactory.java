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
package com.windowtester.internal.swing.locator;

import abbot.finder.Matcher;
import abbot.finder.matchers.ClassMatcher;

import com.windowtester.internal.finder.matchers.swing.CompositeMatcher;
import com.windowtester.internal.finder.matchers.swing.HierarchyMatcher;
import com.windowtester.internal.finder.matchers.swing.IndexMatcher;
import com.windowtester.internal.finder.matchers.swing.LabeledWidgetMatcher;
import com.windowtester.internal.finder.matchers.swing.NameOrLabelMatcher;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.swing.locator.LabeledTextLocator;

/**
 * Builds matchers from locators.
 * @deprecated
 */

public class MatcherFactory {
	
	/**
	 * Generate a Matcher that can be used to identify the widget described
	 * by this WidgetLocator object.
	 * @return a Matcher that matches this object.
	 * @see Matcher
	 */
	public static Matcher getMatcher(WidgetLocator wl) {
		
		Class cls = wl.getTargetClass();
		String nameOrLabel = wl.getNameOrLabel();
		WidgetLocator parentInfo = wl.getParentInfo();
		
		if (wl instanceof LabeledTextLocator) {
			/*
			 * NOTE: Labeled locators are not indexable
			 * 
			 */
			LabeledWidgetMatcher labelMatcher = new LabeledWidgetMatcher(cls, nameOrLabel);
			if (parentInfo == null)
				return labelMatcher;
			
			return new HierarchyMatcher(labelMatcher, getMatcher(parentInfo));
		}
		
		int index = wl.getIndex();
		
		//standard case
		if (parentInfo == null) {
			return getTargetMatcher(wl);
		} else {
			//handle indexed case
			if (index != WidgetLocator.UNASSIGNED) {
				return (nameOrLabel != null) ?
					new HierarchyMatcher(cls, nameOrLabel, index, getMatcher(parentInfo)) :
						new HierarchyMatcher(cls, index, getMatcher(parentInfo));
			} else  { //unindexed
				return (nameOrLabel != null) ?
						new HierarchyMatcher(cls, nameOrLabel, getMatcher(parentInfo)) :
							new HierarchyMatcher(cls, getMatcher(parentInfo));
			}
		}
	}
	


	/**
	 * Get the matcher for the target widget.
	 * @return the target matcher
	 */
	private static Matcher getTargetMatcher(WidgetLocator wl) {
		int index = wl.getIndex();
		String nameOrLabel = wl.getNameOrLabel();
		Class cls = wl.getTargetClass();
		
		//FIXME: refactor and centralize (duplicated in HierarchyMatcher constructor); also notice uses of IndexMatcher -- should be removed...		
		if (index == WidgetLocator.UNASSIGNED) {
			if (nameOrLabel == null) {
				return new ClassMatcher(cls);
			} else
				return new CompositeMatcher(new Matcher[] {
								new ClassMatcher(cls),
								new NameOrLabelMatcher(nameOrLabel) });
		}
		if (nameOrLabel == null) {
			return new IndexMatcher(
							new ClassMatcher(cls), index);
		} else
			return new CompositeMatcher(new Matcher[] {
				new ClassMatcher(cls),
				new IndexMatcher(
						new NameOrLabelMatcher(nameOrLabel), index) });
	}
	

}
