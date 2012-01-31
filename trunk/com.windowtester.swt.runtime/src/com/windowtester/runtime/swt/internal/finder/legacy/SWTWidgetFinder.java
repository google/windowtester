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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;

import com.windowtester.internal.runtime.finder.IWidgetFinder;
import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.UIContextSWT;
import com.windowtester.runtime.swt.internal.finder.matchers.AdapterFactory;

/**
 * An SWT Widget Finder.
 * 
 * @deprecated
 */
public class SWTWidgetFinder implements IWidgetFinder {

	private final IUIContext _ui;
	
	/*
	 * This class is essentially an adapter to the old IWidgetFinder API.
	 * This delegate is responsible for all the find heavy lifting.
	 */
	WidgetFinderService _finderService;
	
	/**
	 * Create a finder for the given display.
	 * @param ui the display to search
	 */
	public SWTWidgetFinder(IUIContext ui) {
		_ui = ui;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime2.finder.IWidgetFinder#findAll(com.windowtester.runtime2.locator.IWidgetLocator)
	 */
	public IWidgetLocator[] findAll(IWidgetLocator locator) {
		Matcher m = new AdapterFactory().adapt(locator);
		WidgetFinderService finderService = getFinderService(((UIContextSWT)_ui).getDisplay());
		Collection matches = finderService.collectMatches(m);
		Collection locators = new ArrayList();
		for (Iterator iter = matches.iterator(); iter.hasNext();) {
			//if the locator implements selection logic, pass it to the reference
			Object ref = (locator instanceof IUISelector) ? 
					WidgetReference.create(iter.next(), (IUISelector)locator) : WidgetReference.create(iter.next());
				
			locators.add(ref);
		}
		return (IWidgetLocator[]) locators.toArray(new IWidgetLocator[]{});
	}


	//low-level convenience --- consider moving
	public Widget[] findAllInScope(IWidgetMatcher matcher, Widget searchScope) {
		WidgetFinderService finderService = getFinderService(((UIContextSWT)_ui).getDisplay());
		Matcher m = new AdapterFactory().adapt(matcher);
		List matches = finderService.collectMatchesIn(m, searchScope);		
		return (Widget[]) matches.toArray(new Widget[]{});
	}
	
	
	
	/**
	 * Get the widget finder that is doing the heavy lifting.
	 */
	WidgetFinderService getFinderService(Display display) {
		if (_finderService == null)
			_finderService = new WidgetFinderService(display);
		return _finderService;
	}

	
	
}
