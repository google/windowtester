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
package com.windowtester.runtime.gef.internal.matchers;

import java.io.Serializable;

import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Adapts an {@link IWidgetLocator} to an {@link IFigureMatcher}.
 */
public class MatcherAdapter implements IFigureMatcher, Serializable {
	
	private static final long serialVersionUID = 1L;

	private final IWidgetLocator locator;

	public MatcherAdapter(IWidgetLocator locator) {
		this.locator = locator;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureMatcher#matches(com.windowtester.runtime.gef.IFigureReference)
	 */
	public boolean matches(IFigureReference figureRef) {
		return locator.matches(figureRef);
	}			
	
}