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

import com.windowtester.runtime.locator.IWidgetMatcher;

import abbot.finder.Matcher;

/**
 * A service for adapting {@link IWidgetMatcher}s to Abbot 
 * {@link Matcher}s and vice versa.
 */
public class AdapterFactory {

	//TODO: consider singleton vs. monostate
	
	/**
	 * Adapt this <code>IWidgetMatcher</code> to a <code>Matcher</code>.
	 */
	public Matcher adapt(IWidgetMatcher matcher) {
		if (matcher instanceof Matcher)
			return (Matcher)matcher;
		return new AbbotFinderMatcherAdapter(matcher);
	}

	/**
	 * Adapt this <code>Matcher</code> to a <code>IWidgetMatcher</code>.
	 */
	public IWidgetMatcher adapt(Matcher matcher) {
		if (matcher instanceof IWidgetMatcher)
			return (IWidgetMatcher)matcher;
		return new WidgetMatcherAdapter(matcher);
	}
	
}
