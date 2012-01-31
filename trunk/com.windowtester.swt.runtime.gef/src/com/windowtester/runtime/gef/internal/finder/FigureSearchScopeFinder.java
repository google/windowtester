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

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.draw2d.internal.Draw2D;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.gef.internal.locator.ScopeFactory;
import com.windowtester.runtime.swt.internal.Context;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;

/**
 * A finder for figure search scope.
 */
public class FigureSearchScopeFinder {

	private static final FigureSearchScopeFinder _finder = new FigureSearchScopeFinder();
	
	public static FigureSearchScopeFinder getInstance() {
		return _finder;
	}
	
	private FigureSearchScopeFinder() {}
	
	
	public IFigureSearchScope getScope(IFigure figure) {
		
		IEditorLocator locator = Draw2D.getFinder().findEditorLocator(Context.GLOBAL.getUI(), figure);
		if (locator == null)
			return ScopeFactory.unscoped();
		
		return ScopeFactory.editor(locator);
	}

	
	
	
}
