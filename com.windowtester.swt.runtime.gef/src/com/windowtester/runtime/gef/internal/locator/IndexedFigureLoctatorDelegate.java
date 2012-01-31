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
package com.windowtester.runtime.gef.internal.locator;

import java.util.Arrays;
import java.util.Comparator;

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;

public class IndexedFigureLoctatorDelegate extends FigureLocatorDelegate {

	private static final long serialVersionUID = -7686492469493568718L;

	private static final IWidgetLocator[] NO_MATCH = new IWidgetLocator[]{};
	
	private final Comparator _comparator;
	private final int _index;


	/**
	 * Create an instance that identifies a figure at the given index in a list of figures that 
	 * match the criteria defined by the given {@link IFigureMatcher} and ordered using the provided
	 * {@link Comparator}.
	 * @param index the index of the match
	 * @param matcher the matcher that defines matching criteria
	 * @param figureComparator the comparator used to order the matches for selection
	 */
	public IndexedFigureLoctatorDelegate(int index, IFigureMatcher matcher,
			Comparator figureComparator) {
		super(matcher);
		Invariants.notNegative(index);
		Invariants.notNull(figureComparator);
		_index = index;
		_comparator = figureComparator;
	}

	public Comparator getComparator() {
		return _comparator;
	}
	
	public int getIndex() {
		return _index;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.locator.AbstractFigureLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		IWidgetLocator[] matches = super.findAll(ui);
		if (matches.length == 0)
			return NO_MATCH;
		
		Arrays.sort(matches, getComparator());
		
		int index = getIndex();
		if (index > matches.length-1)
			return NO_MATCH;
		
		return new IWidgetLocator[]{matches[index]};
	}

}
