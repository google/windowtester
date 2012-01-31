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

import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;

import com.windowtester.runtime.locator.IWidgetMatcher;

/**
 * An adapter from an Abbot {@link Matcher} to an {@link IWidgetMatcher}.
 */
/*package */ class WidgetMatcherAdapter implements IWidgetMatcher {

	private final Matcher _matcher;
	
	private static boolean TRACE_MATCHES = false;

	/**
	 * Create an instance.
	 * @param wm the matcher to adapt.
	 */
	/*package */ WidgetMatcherAdapter(Matcher matcher) {
		_matcher = matcher;
	}

	/** 
	 * Check to see if this adapted matcher matches.
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		if (!(widget instanceof Widget))
			return false;
		trace(widget);
		return _matcher.matches((Widget)widget);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return _matcher.toString();
	}
	
	private void trace(Object widget) {
		if (TRACE_MATCHES)
			System.out.println(toString() + " testing: " + widget + " for match");
	}

}
