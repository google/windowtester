package com.windowtester.swt.condition.eclipse;

import org.eclipse.swt.widgets.Display;

import abbot.finder.swt.Matcher;

import com.windowtester.finder.swt.WidgetFinder;
import com.windowtester.finder.swt.WidgetFinder.MatchResult;
import com.windowtester.swt.WidgetLocator;
import com.windowtester.swt.condition.ICondition;
import com.windowtester.swt.locator.MatcherFactory;

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
public class WidgetShowingCondition implements ICondition {

	private final Display _display;
	private Matcher _matcher;

	/**
	 * Create an instance using the default display.
	 * @param locator the locator to use to identify the widget in question
	 */
	public WidgetShowingCondition(WidgetLocator locator) {
		this(Display.getDefault(), locator);
	}
	
	/**
	 * Create an instance using the specified display.
	 * @param display the display
	 * @param locator the locator to use to identify the widget in question
	 */
	public WidgetShowingCondition(Display display, WidgetLocator locator) {
		_display = display;
		_matcher = MatcherFactory.getMatcher(locator);
	}
	
	/**
	 * Test whether the widget in question is showing.
	 * 
	 * @see com.windowtester.swt.condition.ICondition#test()
	 */
	public boolean test() {
		MatchResult result = new WidgetFinder().find(_display, _matcher);
		return result.getType() == WidgetFinder.MATCH;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "widget matched by: " + _matcher.toString() + " to show";
	}
	
}
