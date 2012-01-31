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

import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.finder.matchers.eclipse.SectionComponentMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.locator.SectionLocator;

/**
 * Matcher builder.
 */
public class SWTMatcherBuilder {

	@SuppressWarnings("unchecked")
	private static final class SWTMatcherAdapter implements ISWTWidgetMatcher {
		private final IWidgetMatcher matcher;

		public SWTMatcherAdapter(IWidgetMatcher matcher) {
			this.matcher = matcher;
		}
		public boolean matches(ISWTWidgetReference<?> widget) {
			return matcher.matches(widget);
		}
	}
	
	
	private final MatchCriteria criteria = new MatchCriteria();
	
	private ISWTWidgetMatcher parentCriteria;
	private int index;
	
	
	public static SWTMatcherBuilder forSpecs(ISWTWidgetMatcher ... criteria){
		return new SWTMatcherBuilder().specify(criteria);
	}
	
	public static ISWTWidgetMatcher buildMatcher(ISWTWidgetMatcher ... criteria) {
		return forSpecs(criteria).build();
	}
	
	
	/**
	 * Add the given criteria to the filter list.
	 * @param criteria - the criteria to add (NOTE: <code>null</code>s are ignored).
	 */
	public SWTMatcherBuilder specify(ISWTWidgetMatcher ... criteria) {
		if (criteria != null)
			this.criteria.addAll(Arrays.asList(criteria));
		return this;
	}
	
	public void specify(Iterable<ISWTWidgetMatcher> criteria) {
		this.criteria.addAll(criteria);
	}
	
	public SWTMatcherBuilder scope(ISWTWidgetMatcher parentCriteria){
		return scope(ChildOfMatcher.UNSPECIFIED_INDEX, parentCriteria);
	}

	//NOTE: will clobber parent if a parent value is already set
	// TODO[pq]: consider updating this to support better chaining

	public SWTMatcherBuilder scope(int index, ISWTWidgetMatcher parentCriteria){
//		if (this.parentCriteria != null)
//			throw new UnsupportedOperationException("parent is already set");
		this.index = index;
		this.parentCriteria = parentCriteria;
		return this;
	}
	
	
	public ISWTWidgetMatcher build() {
		CriteriaMatcher matcher = new CriteriaMatcher(criteria);
		if (parentCriteria == null) {
			return matcher;
		}
		return new ChildOfMatcher(matcher, index, parentCriteria);		
	}

	public Iterable<ISWTWidgetMatcher> criteria() {
		return criteria;
	}

	public void scope(int index, IWidgetMatcher<?> parentInfo) {
		scope(index, adaptToMatcher(parentInfo));
	}

	
	private static ISWTWidgetMatcher adaptToMatcher(IWidgetMatcher<?> matcher) {
		//a special case for sections which as parents perform component matching...
		//TODO: if there are more cases, consider adding a new interface IComponentMatcher
		if (matcher instanceof SectionLocator)
			matcher = SectionComponentMatcher.forLocator((SectionLocator)matcher);
		
		if (matcher instanceof ISWTWidgetMatcher)
			return (ISWTWidgetMatcher) matcher;
		return new SWTMatcherAdapter(matcher);

	}


	
	
}
