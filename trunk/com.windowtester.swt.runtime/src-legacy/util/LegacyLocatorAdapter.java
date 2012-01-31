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
package com.windowtester.runtime.swt.internal.legacy.util;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.finder.matchers.AdapterFactory;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.swt.WidgetLocator;
import com.windowtester.swt.locator.MatcherFactory;

/**
 * Adapts a {@link WidgetLocator} to a {@link IWidgetLocator}.
 * <p>
 * It uses the basic default {@link SWTWidgetLocator} click and contextClick
 * implementation.  It's matcher is provided by the legacy {@link MatcherFactory}. 
 *
 * @author Phil Quitslund
 *
 */
public class LegacyLocatorAdapter extends SWTWidgetLocator implements ILegacyLocatorAdapter {

	private static final long serialVersionUID = -2918439564502005443L;

	//the wrapped locator
	private final WidgetLocator _adaptedlocator;

	public LegacyLocatorAdapter(WidgetLocator legacyLocator) {
		super(legacyLocator.getTargetClass());
		_adaptedlocator = legacyLocator;
	}

	public WidgetLocator getAdaptedlocator() {
		return _adaptedlocator;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		//WLs support findAll
		return getAdaptedlocator().findAll(ui);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return getMatcher().matches(widget);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected IWidgetMatcher buildMatcher() {
		abbot.finder.swt.Matcher matcher = MatcherFactory.getMatcher(getAdaptedlocator());
		if (matcher == null)
			throw new IllegalStateException("unable to build legacy matcher for " + getAdaptedlocator());
		return new AdapterFactory().adapt(matcher);
	}
	
}
