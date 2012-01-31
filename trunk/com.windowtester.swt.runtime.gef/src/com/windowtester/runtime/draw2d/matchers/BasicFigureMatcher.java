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
package com.windowtester.runtime.draw2d.matchers;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.draw2d.internal.matchers.CompositeMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.IFigureMatcher;

/**
 * A convenience base class for matchers that match on figure instances.
 */
public abstract class BasicFigureMatcher implements IFigureMatcher {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureMatcher#matches(com.windowtester.runtime.gef.IFigureReference)
	 */
	public final boolean matches(IFigureReference figure) {
		if (figure == null)
			return false;
		return matches(figure.getFigure());
	}

	/**
	 * Check whether the given figure satisfies the specified criteria.
	 * 
	 * @param figure the figure instance to test
	 * @return <code>true</code> if the figure matches,
	 * 		<code>false</code> otherwise
	 */
	protected abstract boolean matches(IFigure figure);
	
	
	/**
	 * Build a matcher that composes this matcher's criteria with that
	 * of the given matcher. 
	 * 
	 * @param matcher a matcher to compose
	 * @return a new composite matcher
	 */
	public IFigureMatcher and(IFigureMatcher matcher) {
		return new CompositeMatcher(this, matcher);
	}

}
