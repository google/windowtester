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
package com.windowtester.runtime.gef.internal.finder.scope;

import java.io.Serializable;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * An unscoped widget search.
 */
public class UnscopedSearch implements IFigureSearchScope, Serializable{
		
	private static final long serialVersionUID = 5377194462233705511L;

	private static final UnscopedSearch INSTANCE = new UnscopedSearch();

	public static IFigureSearchScope getInstance() {
		return INSTANCE;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.internal.finder.IFigureSearchScope#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IFigureMatcher matcher) {
		return Draw2DFinder.getDefault().findAllFigureLocators(ui, matcher);
	}


}
