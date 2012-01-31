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
package com.windowtester.runtime.swt.internal.widgets.finder;

import java.util.concurrent.Callable;

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.swt.UnableToFindActiveShellException;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.finder.RetrySupport;
import com.windowtester.runtime.swt.internal.matchers.IsVisibleMatcher;
import com.windowtester.runtime.swt.internal.matchers.SWTMatcherBuilder;
import com.windowtester.runtime.swt.internal.settings.TestSettings;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.ISearchable;

public class SWTWidgetFinder {


	public static enum Filter implements ISWTWidgetMatcher {
		VISIBLES(IsVisibleMatcher.forValue(true));
		
		private final ISWTWidgetMatcher matcher;

		Filter(ISWTWidgetMatcher matcher){
			this.matcher = matcher;
		}		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
		 */
		public boolean matches(ISWTWidgetReference<?> widget) {
			return matcher.matches(widget);
		}
	}
	
	
	public static class Scope {
		public static ISearchable activeShell(){
			//add retries
			return (ISearchable)RetrySupport.retryUntilResultIsNonNull(new RunnableWithResult() {
				@Override
				public Object runWithResult() {
					return display().getActiveShell();
				}
			});	
			// TODO[pq]: don't like this cast but at this point not sure if we want searchables to get into API
//			return (ISearchable) display().getActiveShell();
		}
		public static DisplayReference display(){
			return DisplayReference.getDefault();
		}
	}
	
	
	private ISearchable scope               = Scope.display();                /* default */
//	private List<ISWTWidgetMatcher> filters = new ArrayList<ISWTWidgetMatcher>(); 
		
	
	private SWTWidgetFinder(){
		
	}
	
	public static SWTWidgetFinder forActiveShell(){
		return new SWTWidgetFinder().withScope(Scope.activeShell());
	}
	
	public static SWTWidgetFinder forDisplay() {
		return new SWTWidgetFinder().withScope(Scope.display());
	}
	
	/**
	 * Set the search scope.
	 * @param scope the scope of the search (for example {@link Scope#activeShell()}).
	 * @return the updated Finder
	 */
	public SWTWidgetFinder withScope(ISearchable scope){
		this.scope = scope;
		return this;
	}
	
	/**
	 * Setup filters for this finder.  Filters are checked <b>before</b> search criteria.  Typical filters are defined in the {@link Filter} <code>enum</code>.
	 * @param filter the filter to add
	 * @return the Finder
	 */
	public SWTWidgetFinder withFilter(ISWTWidgetMatcher ... filter){
		Invariants.notNull(filter);
		
		throw new UnsupportedOperationException();
//		filters.addAll(Arrays.asList(filter));
//		return this;
	}
	
	
	/**
	 * Perform the search.
	 */
	public ISWTWidgetReference<?>[] findAll(ISWTWidgetMatcher ... crits){
		ISWTWidgetMatcher matcher = (crits.length == 1) ? crits[0] : SWTMatcherBuilder.buildMatcher(crits);
		return doFindAll(matcher);
	}
	

	private ISWTWidgetReference<?>[] doFindAll(ISWTWidgetMatcher criteria){
		final ISWTWidgetMatcher filteredMatcher = addFilters(criteria);
		return DisplayReference.getDefault().execute(new Callable<ISWTWidgetReference<?>[]>() {
			public ISWTWidgetReference<?>[] call() throws Exception {
				if (scope == null){
//					throw new IllegalStateException("search scope is null");
					throw new UnableToFindActiveShellException();
				}
				return scope.findWidgets(filteredMatcher);
			}
		});
		
	}

	private ISWTWidgetMatcher addFilters(ISWTWidgetMatcher criteria) {
		
		return criteria;
	}

	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// (Legacy) Accessors
	// TODO[pq]: assess where these should live (or if they should be removed altogether)
	//
	/////////////////////////////////////////////////////////////////////////////////
	
	//number of times to retry a widget find
	public static int getMaxFinderRetries() {
		return TestSettings.getInstance().getFinderRetries();
	}
	
	public static int getFinderRetryInterval() {
		return TestSettings.getInstance().getFinderRetryInterval();
	}
	

	
}
