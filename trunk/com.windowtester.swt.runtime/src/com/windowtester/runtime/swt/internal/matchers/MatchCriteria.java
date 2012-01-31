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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;

/**
 * Encapsulates a list of match criteria.
 */
public class MatchCriteria implements Iterable<ISWTWidgetMatcher>{

//	@SuppressWarnings("unchecked")
//	private static final List<Class<? extends WidgetMatcher>> TO_TEST = Arrays.asList(IsVisibleMatcher.class, ByTextMatcher.class, ByClassMatcher.class);
	
	// TODO[pq]: if we incorporate google collections, re-work
	
	private final List<ISWTWidgetMatcher> criteria = new ArrayList<ISWTWidgetMatcher>();
	
//	Predicate<ISWTWidgetMatcher> CONTAINS = new Predicate<ISWTWidgetMatcher>() {
//		public boolean apply(ISWTWidgetMatcher input) {
//			for(ISWTWidgetMatcher crit : criteria) {
//				if (isEq(crit, input))
//					return true;
//			}
//			return false;
//		}
//	};

	public MatchCriteria() {
	}

	public MatchCriteria(Iterable<ISWTWidgetMatcher> criteria) {
		addAll(criteria);
	}

	public MatchCriteria add(ISWTWidgetMatcher crit){
//		if (!CONTAINS.apply(crit))
			criteria.add(crit);
		return this;
	}
	
	public MatchCriteria addAll(Iterable<ISWTWidgetMatcher> criteria) {
		if (criteria != null) {
			for (ISWTWidgetMatcher crit : criteria) {
				add(crit);
			}
		}
		return this;
	}
	
	
//	//quick and dirty comparison of matchers
//	private static boolean isEq(ISWTWidgetMatcher o1, ISWTWidgetMatcher o2) {
//		//narrowing down our comparison to avoid excluding composites
//		if (!TO_TEST.contains(o1.getClass()))
//			return false;
//		return (o1.getClass() == o2.getClass());
//	}


	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ISWTWidgetMatcher> iterator() {
		return criteria.iterator();
	}



	
}
