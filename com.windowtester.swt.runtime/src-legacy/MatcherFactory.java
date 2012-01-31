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
package com.windowtester.swt.locator;

import abbot.finder.matchers.swt.CompositeMatcher;
import abbot.finder.matchers.swt.IndexMatcher;
import abbot.finder.swt.Matcher;

import com.windowtester.runtime.swt.internal.abbot.matcher.ExactClassMatcher;
import com.windowtester.runtime.swt.internal.abbot.matcher.HierarchyMatcher;
import com.windowtester.runtime.swt.internal.abbot.matcher.LabeledWidgetMatcher;
import com.windowtester.runtime.swt.internal.abbot.matcher.NameOrLabelMatcher;
import com.windowtester.runtime.swt.internal.abbot.matcher.ShellComponentMatcher;
import com.windowtester.runtime.swt.internal.abbot.matcher.ViewComponentMatcher;
import com.windowtester.swt.WidgetLocator;
import com.windowtester.swt.locator.eclipse.ViewLocator;

/**
 * Builds matchers from locators.
 * 
 * @author Phil Quitslund
 *
 */
public class MatcherFactory {
	/**
	 * Generate a Matcher that can be used to identify the widget described
	 * by this WidgetLocator object.
	 * @return a Matcher that matches this object.
	 * @see Matcher
	 */
	public static Matcher getMatcher(WidgetLocator wl) {
		
		/*
		 * UGLY!: special casing
		 * ALSO NOTICE: not set up to nest special scope locators
		 * should we?
		 */
		if (wl instanceof ViewLocator) {
			//notice the name carries the view id
			return new ViewComponentMatcher(((ViewLocator)wl).getNameOrLabel());	
		}
		
		if (wl instanceof ShellLocator) {
			ShellLocator locator = (ShellLocator)wl;
			return new ShellComponentMatcher(locator.getNameOrLabel(), locator.isModal());
		}
		
		
		Class cls = wl.getTargetClass();
		String nameOrLabel = wl.getNameOrLabel();
		WidgetLocator parentInfo = wl.getParentInfo();
		
		if (wl instanceof LabeledLocator) {
			/*
			 * NOTE: Labeled locators are not indexable
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
				return new ExactClassMatcher(cls);
			} else
				return new CompositeMatcher(new Matcher[] {
								new ExactClassMatcher(cls),
								new NameOrLabelMatcher(nameOrLabel) });
		}
		if (nameOrLabel == null) {
			return new IndexMatcher(
							new ExactClassMatcher(cls), index);
		} else
			return new CompositeMatcher(new Matcher[] {
				new ExactClassMatcher(cls),
				new IndexMatcher(
						new NameOrLabelMatcher(nameOrLabel), index) });
	}
	
	
}
