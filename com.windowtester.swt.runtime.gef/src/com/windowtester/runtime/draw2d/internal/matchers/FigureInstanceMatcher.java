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

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.draw2d.matchers.BasicFigureMatcher;

public class FigureInstanceMatcher extends BasicFigureMatcher {
	
	private final IFigure _instance;

	public FigureInstanceMatcher(IFigure instance) {
		_instance = instance;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IFigureMatcher#matches(org.eclipse.draw2d.IFigure)
	 */
	public boolean matches(IFigure figure) {
		return figure == _instance;
	}

}
