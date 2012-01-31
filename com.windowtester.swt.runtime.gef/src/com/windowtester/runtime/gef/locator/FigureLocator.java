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
package com.windowtester.runtime.gef.locator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;

import com.windowtester.runtime.draw2d.internal.matchers.VisibilityMatcher;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.internal.locator.DelegatingLocator;
import com.windowtester.runtime.gef.internal.locator.FigureLocatorDelegate;
import com.windowtester.runtime.gef.internal.locator.ScopeFactory;
import com.windowtester.runtime.swt.locator.eclipse.IWorkbenchPartLocator;

/**
 * Locates {@link Figure} references on a {@link FigureCanvas}.
 * <p>
 * Figures are identified by matching criteria on the figure via an instance of
 * {@link IFigureMatcher}.
 * <p>
 * By default search for figures is unscoped, but figure search can be
 * explicitly scoped by a workbench part (via a scoping {@link IWorkbenchPartLocator}).
 * @see IFigureMatcher
 */
public class FigureLocator extends DelegatingLocator {
	
	private static final long serialVersionUID = 3519809456851534842L;

	/**
	 * Create an unscoped instance parameterized by the given matcher.
	 * 
	 * @param matcher the matcher
	 */
	public FigureLocator(IFigureMatcher matcher) {
		super(new FigureLocatorDelegate(VisibilityMatcher.isVisible(matcher)));
	}

	/**
	 * Create a scoped instance parameterized by the given matcher.  Figure lookup 
	 * (triggered by calls to: {@link FigureLocator#findAll(com.windowtester.runtime.IUIContext)})
	 * will be scoped by the workbench part identified by the specified
	 * {@link IWorkbenchPartLocator}.
	 * 
	 * @param matcher the matcher
	 * @param partScope a part locator identifying the workbench part that
	 * should scope figure lookup
	 */
	public FigureLocator(IFigureMatcher matcher, IWorkbenchPartLocator partScope) {
		super(new FigureLocatorDelegate(VisibilityMatcher.isVisible(matcher), ScopeFactory.figureScopeForPart(partScope)));
	}

}
