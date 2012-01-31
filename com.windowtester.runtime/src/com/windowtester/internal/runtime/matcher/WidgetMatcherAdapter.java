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
package com.windowtester.internal.runtime.matcher;

import java.awt.Component;

import abbot.finder.Matcher;

import com.windowtester.runtime.locator.IWidgetMatcher;

/**
 * An adapter from an Abbot {@link Matcher} to an {@link IWidgetMatcher}.
 * <p>
 * Created using the {@link AdapterFactory#adapt(Matcher)} creation
 * method.
 */
public class WidgetMatcherAdapter implements IWidgetMatcher {

	private final Matcher matcher;

	/**
	 * Create an instance.
	 * @param wm the matcher to adapt.
	 */
	public  WidgetMatcherAdapter(Matcher matcher) {
		this.matcher = matcher;
	}

	/** 
	 * Check to see if this adapted matcher matches.
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		//TODO: does this type check belong in core?
		if (!(widget instanceof Component))
			return false;
		return matcher.matches((Component)widget);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return matcher.toString();
	}

}
