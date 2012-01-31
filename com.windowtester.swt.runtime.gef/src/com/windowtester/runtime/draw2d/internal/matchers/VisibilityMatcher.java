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
package com.windowtester.runtime.draw2d.internal.matchers;

import java.io.Serializable;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.draw2d.matchers.BasicFigureMatcher;
import com.windowtester.runtime.gef.IFigureMatcher;

/**
 * Matches only visibly figures.
 */
public class VisibilityMatcher extends BasicFigureMatcher implements Serializable  {

	private static final long serialVersionUID = 8805005734641152107L;

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.matchers.BasicFigureMatcher#matches(org.eclipse.draw2d.IFigure)
	 */
	protected boolean matches(IFigure figure) {
		if (figure == null)
			return false;
		return isVisible(figure);
	}

	protected boolean isVisible(IFigure figure) {
		return figure.isShowing(); //figure.isVisibile() is less strict
	}
	
	public static IFigureMatcher isVisible(IFigureMatcher matcher) {
		return new VisibilityMatcher().and(matcher);
	}
	
	
}
