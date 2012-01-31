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

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScopeable;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * A simple scoped figure finder.
 */
public class ScopedFigureFinder implements IFigureFinder, IFigureSearchScopeable {

	private final IFigureSearchScope _searchScope;

	public ScopedFigureFinder(IFigureSearchScope searchScope) {
		Invariants.notNull(searchScope);
		_searchScope = searchScope;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.internal.finder.IFigureSearchScopeable#getSearchScope()
	 */
	public IFigureSearchScope getSearchScope() {
		return _searchScope;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.IFigureFinder#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IFigureMatcher matcher) {
		return getSearchScope().findAll(ui, matcher);
	}
	
}
