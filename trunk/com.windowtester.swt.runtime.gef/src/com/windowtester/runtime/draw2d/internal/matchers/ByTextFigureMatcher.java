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
import com.windowtester.runtime.gef.internal.util.TextHelper;

public class ByTextFigureMatcher extends BasicFigureMatcher {

	private final String _text;

	public ByTextFigureMatcher(String textOrPattern) {
		_text = textOrPattern;
	}

	public final String getText() {
		return _text;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IFigureMatcher#matches(org.eclipse.draw2d.IFigure)
	 */
	public boolean matches(IFigure figure) {
		return TextHelper.textMatches(figure, getText());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ByTextFigureMatcher[" + getText() +"]";
	}
	
	
}
