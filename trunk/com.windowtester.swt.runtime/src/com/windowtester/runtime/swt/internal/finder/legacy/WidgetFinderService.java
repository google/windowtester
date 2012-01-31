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
package com.windowtester.runtime.swt.internal.finder.legacy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;
import abbot.finder.swt.SWTHierarchy;

import com.windowtester.internal.runtime.finder.IWidgetFinder;
import com.windowtester.runtime.swt.UnableToFindActiveShellException;
import com.windowtester.runtime.swt.internal.finder.legacy.WidgetFinder.MatchResult;
import com.windowtester.runtime.util.ScreenCapture;

/**
 * A basic implementation of an {@link IWidgetFinder} for public consumption. 
 * @deprecated
 */
public class WidgetFinderService {


	private final WidgetFinder _finder;
	private final Display _display;
    private SearchScopeHelper _searchScopeHelper;
	
	public WidgetFinderService(Display display) {
		_finder  = new WidgetFinder();
		_display = display;
	}
	
	
//
//	/* (non-Javadoc)
//	 * @see com.windowtester.swt.IWidgetFinder#find(com.windowtester.runtime.WidgetLocator)
//	 */
//	public Collection find(WidgetLocator wl) {
//
//		/*
//		 * This is NOT CLEAN.  We need to fix this post 2.0.
//		 */
//		if (!(wl instanceof com.windowtester.swt.WidgetLocator))
//			throw new IllegalArgumentException("expected argument of type: com.windowtester.swt.WidgetLocator");
//		
//		Matcher matcher = MatcherFactory.getMatcher((com.windowtester.swt.WidgetLocator)wl);
//		return collectMatches(matcher);
//	}


	public List collectMatches(Matcher matcher) {
	
		/*
		 * In the future we may want this to be use configurable (e.g, shell scope or not
		 * and number of retries
		 */
		Shell searchScope = getSearchScopeHelper().getShellSearchScope(matcher);
		
		return collectMatchesIn(matcher, searchScope);
	}

	
	public List collectMatchesIn(Matcher matcher, Widget searchScope) {
		//System.out.println("searching in scope: " + UIProxy.getToString(searchScope));
		
		MatchResult result = null;
		try {
			result = _finder.find(searchScope /*_display*/, matcher, 0 /* no retries */);
		} catch(RootWidgetIsNullError e) {
			ScreenCapture.createScreenCapture();
			throw new UnableToFindActiveShellException();
		}
		
		
		List matches = new ArrayList();
		
		switch(result.getType()) {
		case WidgetFinder.MULTIPLE_WIDGETS_FOUND :
			matches.addAll(result.getWidgets());
			break;
		case WidgetFinder.MATCH :
			matches.add(result.getWidget());
			break;
		case WidgetFinder.WIDGET_NOT_FOUND :
			break;
		}

		return matches;
		
	}
	
	
	
	private SearchScopeHelper getSearchScopeHelper() {
		if (_searchScopeHelper == null)
			_searchScopeHelper = new SearchScopeHelper(new SWTHierarchy(_display));
		return _searchScopeHelper;
	}
	
}
