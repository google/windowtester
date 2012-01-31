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
package com.windowtester.runtime.gef.internal.experimental.factory;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.draw2d.matchers.BasicFigureMatcher;
import com.windowtester.runtime.internal.IMatcher;
import com.windowtester.runtime.internal.matcher.EqualityMatcher;
import com.windowtester.runtime.internal.matcher.PropertyMatcher;

public class ByStringPropertyFigureMatcher extends BasicFigureMatcher {

	private final IMatcher matcher;
	
	public ByStringPropertyFigureMatcher(String propertyName, String propertyValue) {
		matcher = new PropertyMatcher(propertyName, new EqualityMatcher(propertyValue));
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.matchers.BasicFigureMatcher#matches(org.eclipse.draw2d.IFigure)
	 */
	protected boolean matches(IFigure figure) {
		return matcher.matches(figure);
	}

}
