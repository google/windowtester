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

import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.swt.locator.eclipse.IWorkbenchPartLocator;

/**
 * Locates {@link Figure} references by matching their class.
 * <p>
 * By default search for figures is unscoped, but figure search can be
 * explicitly scoped by a workbench part (via a scoping {@link IWorkbenchPartLocator}).
 * 
 * @see IFigureMatcher
 */
public class FigureClassLocator extends FigureLocator {

	//TODO: move to API package
		
	private static final long serialVersionUID = -3216735309729870403L;

	private final String className;

	/**
	 * Create an unscoped instance that identifies figures of the given class.
	 * 
	 * @param className the name of the figure class to locate
	 */
	public FigureClassLocator(String className) {
		super(new ByClassNameFigureMatcher(className));
		this.className = className;
	}

	/**
	 * Create a scoped instance parameterized by the given matcher.  Figure lookup 
	 * (triggered by calls to: {@link FigureClassLocator#findAll(com.windowtester.runtime.IUIContext)})
	 * will be scoped by the workbench part identified by the specified
	 * {@link IWorkbenchPartLocator}.
	 * 
	 * @param className the name of the figure class to locate
	 * @param partScope a part locator identifying the workbench part that
	 * should scope figure lookup
	 */
	public FigureClassLocator(String className, IWorkbenchPartLocator partScope) {
		super(new ByClassNameFigureMatcher(className), partScope);
		this.className = className;
	}
	
	public String getClassName() {
		return className;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "FigureClassLocator(" + getClassName() + ")";
	}
	
	
}
