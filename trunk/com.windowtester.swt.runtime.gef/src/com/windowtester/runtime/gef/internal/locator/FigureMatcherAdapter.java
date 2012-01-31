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
package com.windowtester.runtime.gef.internal.locator;

import java.io.Serializable;

import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.locator.ILocator;

public class FigureMatcherAdapter implements IFigureMatcher, Serializable {

	private static final long serialVersionUID = 72087906720352748L;

	static final class NoMatcher implements IFigureMatcher {

		public boolean matches(IFigureReference figureRef) {
			return false;
		}	
	}
	
	private final ILocator locator;
	private IFigureMatcher matcher;


	private FigureMatcherAdapter(ILocator locator) {
		this.locator = locator;
	}
	
	private IFigureMatcher getMatcher() {
		if (matcher == null)
			matcher = createMatcher();
		return matcher;
	}
	
	private IFigureMatcher createMatcher() {
		if (!(locator instanceof IAdaptable))
			return new NoMatcher();
		IAdaptable adapter = (IAdaptable)locator;
		IFigureMatcher matcher = (IFigureMatcher) adapter.getAdapter(IFigureMatcher.class);
		if (matcher == null)
			return new NoMatcher();
		return matcher;
	}

	public boolean matches(IFigureReference figureRef) {
		return getMatcher().matches(figureRef);
	}

	public static IFigureMatcher forLocator(ILocator locator) {
		return new FigureMatcherAdapter(locator);
	}
	
	
}