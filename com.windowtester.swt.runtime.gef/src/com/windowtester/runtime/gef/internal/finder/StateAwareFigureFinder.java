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
package com.windowtester.runtime.gef.internal.finder;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.internal.helpers.IStatefulMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * A scoped finder that clears matcher state before performing finds.
 */
public class StateAwareFigureFinder extends ScopedFigureFinder {

	public StateAwareFigureFinder(IFigureSearchScope scope) {
		super(scope);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.IFigureFinder#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IFigureMatcher matcher) {
		
		/*
		 * For performance, we allow for caching.  A consequence is that we need to be 
		 * careful to clear cache before all searches.
		 */
		clearCachedMatcherData(matcher);
		return super.findAll(ui, matcher);
	}

	private void clearCachedMatcherData(IFigureMatcher matcher) {
		if (matcher instanceof IStatefulMatcher)
			((IStatefulMatcher)matcher).clearCache();
	}
	
}
