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
package com.windowtester.runtime.internal.finder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.ISearchable;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;

/**
 * NOTE: ultimately to replace: com.windowtester.finder.swt.SWTWidgetFinder
 */
public class BasicWidgetFinder {

	//TODO: surface way to configure retries...
	
	private static final Widget[] EMPTY_LIST = new Widget[]{};
	
	public Widget[] findAll(Widget root, IWidgetMatcher matcher) {
//		Matcher legacyMatcher = new AdapterFactory().adapt(matcher);
//		return collectMatches(root, legacyMatcher);
		IWidgetLocator[] locators = findAllLocators(root, matcher);
		List<Widget> widgets = new ArrayList<Widget>();
		for (int i = 0; i < locators.length; i++) {
			widgets.add(((ISWTWidgetReference<Widget>)locators[i]).getWidget());
		}
		return widgets.toArray(EMPTY_LIST);
		
	}

	public IWidgetLocator[] findAllLocators(Widget root, IWidgetMatcher matcher) {
		
		ISWTWidgetReference<?> rootRef = (ISWTWidgetReference<?>) WTRuntimeManager.asReference(root);
		ISWTWidgetMatcher swtMatcher   = adaptMatcher(matcher);
		if (swtMatcher == null)
			throw new IllegalStateException("matcher: " + matcher + " must adapt to the ISWTWidgetMatcher interface");
		return SWTWidgetFinder.forActiveShell().withScope((ISearchable) rootRef).findAll((swtMatcher));
		
//		return adaptWidgetsToLocators(findAll(root, matcher));
	}

	private ISWTWidgetMatcher adaptMatcher(IWidgetMatcher matcher) {
		if (matcher instanceof ISWTWidgetMatcher)
			return (ISWTWidgetMatcher) matcher;
		if (matcher instanceof IAdaptable)
			return (ISWTWidgetMatcher) ((IAdaptable)matcher).getAdapter(ISWTWidgetMatcher.class);
		return null;
	}
	
//	public Widget[] collectMatches(Widget searchRooot, Matcher matcher) {
//		
//		MatchResult result = new WidgetFinder().find(searchRooot, matcher);
//
//		List matches = new ArrayList();
//		
//		switch(result.getType()) {
//		case WidgetFinder.MULTIPLE_WIDGETS_FOUND :
//			matches.addAll(result.getWidgets());
//			break;
//		case WidgetFinder.MATCH :
//			matches.add(result.getWidget());
//			break;
//		case WidgetFinder.WIDGET_NOT_FOUND :
//			break;
//		}
//
//		return (Widget[]) matches.toArray(EMPTY_LIST);
//	}
//	
//	private static IWidgetLocator[] adaptWidgetsToLocators(Widget[] widgets) {
//		List locators = new ArrayList();
//		for (int i = 0; i < widgets.length; i++) {
//			locators.add(WidgetReference.create((Widget)widgets[i]));
//		}
//		return (IWidgetLocator[]) locators.toArray(new IWidgetLocator[]{});
//	}
	
}
