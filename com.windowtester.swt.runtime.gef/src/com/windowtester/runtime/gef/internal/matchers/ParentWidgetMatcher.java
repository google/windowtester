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

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.draw2d.matchers.BasicFigureMatcher;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.Context;

public class ParentWidgetMatcher extends BasicFigureMatcher {

	private final IWidgetMatcher _matcher;
	//TODO: this ui context ref is not kosher ... see about removing it...
	private transient IUIContext _ui;

	public ParentWidgetMatcher(IWidgetMatcher matcher) {
		_matcher = matcher;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IFigureMatcher#matches(org.eclipse.draw2d.IFigure)
	 */
	public boolean matches(IFigure figure) {
		return _matcher.matches(getParent(figure));
	}

	private FigureCanvas getParent(IFigure figure) {
		return Draw2DFinder.getDefault().findParentCanvas(getUI(), figure);
	}

	private IUIContext getUI() {
		if (_ui == null)
			_ui = Context.GLOBAL.getUI();
		return _ui;
	}

}
