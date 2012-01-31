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
package com.windowtester.runtime.gef.internal.identifier;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Event;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.Context;

/**
 * Basic class-based, l-to-r indexing identifier.
 */
public class SimpleClassBasedFigureIdentifier implements IFigureIdentifier {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure, org.eclipse.swt.widgets.Event)
	 */
	public ILocator identify(IFigure figure, Event event) {
		if (figure == null)
			return null;

		FigureClassLocator baseLocator = buildBaseLocator(figure);		
		return buildQualifiedLocator(figure, event, baseLocator);
	}

	private ILocator buildQualifiedLocator(IFigure figure, Event event,
			FigureClassLocator clsLocator) {
		IWidgetLocator[] matches = clsLocator.findAll(getUI());
		if (matches.length == 0)
			return null;
		if (matches.length == 1)
			return clsLocator;
		return new LRQualifyingIdentifier(clsLocator).identify(figure, event);
	}

	private FigureClassLocator buildBaseLocator(IFigure figure) {
		Class cls = figure.getClass();
		FigureClassLocator clsLocator = new FigureClassLocator(cls.getName());
		return clsLocator;
	}

	private IUIContext getUI() {
		return Context.GLOBAL.getUI();
	}

}
