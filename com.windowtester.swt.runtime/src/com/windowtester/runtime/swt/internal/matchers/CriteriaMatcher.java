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

import java.util.Arrays;

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * A matcher for building other matchers.
 */
public class CriteriaMatcher implements ISWTWidgetMatcher {

	private final MatchCriteria criteria;
	
	public CriteriaMatcher(MatchCriteria criteria) {
		this.criteria = new MatchCriteria(criteria);
	}

	public CriteriaMatcher() {
		this(new MatchCriteria());
	}

	/**
	 * Add the given criteria to the filter list.
	 * @param criteria - the criteria to add (NOTE: <code>null</code>s are ignored).
	 */
	public CriteriaMatcher addCriteria(ISWTWidgetMatcher ... criteria) {
		if (criteria != null)
			this.criteria.addAll(Arrays.asList(criteria));
		return this;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> widget) {
		for (ISWTWidgetMatcher matcher : criteria) {
			if (!matcher.matches(widget))
				return false;
		}
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return criteria.toString();
	}

	/**
	 * Create a new matcher with the same criteria as this one.
	 */
	public CriteriaMatcher copy() {
		return new CriteriaMatcher(criteria);
	}
}
