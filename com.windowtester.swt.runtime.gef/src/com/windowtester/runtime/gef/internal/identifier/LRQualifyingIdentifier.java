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

import java.util.Arrays;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Event;

import com.windowtester.internal.debug.LogHandler;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.internal.locator.XYComparator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.gef.locator.LRLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.Context;

/**
 * An identifier that qualifies a locator match with left-to-right index
 * info (wrapped in an LRLocator).
 */
public class LRQualifyingIdentifier implements IFigureIdentifier {

	private static final int INVALID = -1;
	private final FigureLocator locator;

	public LRQualifyingIdentifier(FigureLocator locator) {
		this.locator = locator;
	}
	
	public FigureLocator getLocator() {
		return locator;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure, org.eclipse.swt.widgets.Event)
	 */
	public ILocator identify(IFigure figure, Event event) {
		FigureLocator locator = getLocator();
		IWidgetLocator[] matches = Context.GLOBAL.getUI().findAll(locator);
		/*
		 * sanity checks
		 */
		if (isZero(matches))
			return null;
		if (isOne(matches))
			return matches[0];
		
		int index = findIndex(figure, matches);
		if (isInvalid(index)) {
			LogHandler.log(new IllegalStateException("invalid index"));
			return null;
		}
		
		return new LRLocator(index, locator);
	}

	private boolean isInvalid(int index) {
		return index == INVALID;
	}

	private boolean isOne(IWidgetLocator[] matches) {
		return matches.length == 1;
	}

	private boolean isZero(IWidgetLocator[] matches) {
		return matches.length == 0;
	}

	public static int findIndex(IFigure figure, IWidgetLocator[] matches) {
		
		Arrays.sort(matches, new XYComparator());
		IFigureReference ref = null;
		
		for (int index = 0; index < matches.length; index++) {
			ref = (IFigureReference)matches[index];
			if (ref.getFigure() == figure)
				return index;
		}
		return INVALID;
	}

}
