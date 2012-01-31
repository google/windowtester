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
package com.windowtester.runtime.gef.internal.finder;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Event;

import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.Context;

public class ConnectionAwareCanvasItemIdentifier implements IFigureIdentifier {

	
	static final class Proposers {
		static IFigureMatcherProposer className() {
			return new IFigureMatcherProposer() {
				public IFigureMatcher propose(IFigureSearchScope scope, IFigure figure) {
					return new ByClassNameFigureMatcher(figure.getClass().getName());
				}
			};
		}
		
		
	}
	
	
	
	
	//setup proposal registry
	private final FigureMatcherProposerList _props = new FigureMatcherProposerList();
	{
		_props.add(Proposers.className());
	}
	
	private FigureMatcherProposerList getProposers() {
		return _props;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.finder.IFigureIdentifier#identify(com.windowtester.runtime.draw2d.internal.internal.finder.IFigureSearchScope, org.eclipse.draw2d.IFigure)
	 */
	public ILocator identify(IFigure figure, Event event) {
		
		if (figure == null)
			return null;
		
		IFigureSearchScope scope = getScope(figure);
		
		IFigureMatcher matcher = getProposers().propose(scope, figure);
		if (matcher == null)
			return null;
		
		if (isUnique(matcher))
			return locator(matcher);
		
		
		return null;
	}

	private IWidgetLocator locator(IFigureMatcher matcher) {
		return new FigureLocator(matcher);
	}


	private IFigureSearchScope getScope(IFigure figure) {
		return FigureSearchScopeFinder.getInstance().getScope(figure);
	}

	private boolean isUnique(IFigureMatcher matcher) {
		IWidgetLocator[] found = Draw2DFinder.getDefault().findAllFigureLocators(Context.GLOBAL.getUI(), matcher);
		return found.length == 1;
	}

}
