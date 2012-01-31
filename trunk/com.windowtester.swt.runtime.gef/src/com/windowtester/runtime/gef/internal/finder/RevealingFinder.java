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

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.internal.selectors.IRevealer;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * A revealing finder performs a reveal action before it attempts to find.
 */
public class RevealingFinder implements IFigureFinder {

	private final IFigureFinder _finder;
	private final IRevealer _revealer;

	public RevealingFinder(IFigureFinder finder, IRevealer revealer) {
		Invariants.notNull(finder);
		Invariants.notNull(revealer);
		_finder = finder;
		_revealer = revealer;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.IFigureFinder#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IFigureMatcher matcher) {
		try {
			getRevealer().reveal(ui);
		} catch (WidgetSearchException e) {
			return new IWidgetLocator[]{}; //no match --->TODO: it may be handy to pass this exception back?
		}
		return getFinder().findAll(ui, matcher);
	}

	private IRevealer getRevealer() {
		return _revealer;
	}

	private IFigureFinder getFinder() {
		return _finder;
	}

}
