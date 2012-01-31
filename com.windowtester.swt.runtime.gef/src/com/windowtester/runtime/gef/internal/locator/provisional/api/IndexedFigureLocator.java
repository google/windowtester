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
package com.windowtester.runtime.gef.internal.locator.provisional.api;

import java.util.Comparator;

import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.internal.locator.DelegatingLocator;
import com.windowtester.runtime.gef.internal.locator.IndexedFigureLoctatorDelegate;

/**
 * A special locator that chooses an indexed match in an ordered 
 * list of figure matches.
 */
public class IndexedFigureLocator extends DelegatingLocator {

	private static final long serialVersionUID = 3269347980892515300L;

	/**
	 * Create an instance that identifies a figure at the given index in a list of figures that 
	 * match the criteria defined by the given {@link IFigureMatcher}, ordered using the 
	 * provided {@link Comparator}.
	 * @param index the index of the match
	 * @param matcher the matcher that defines matching criteria
	 * @param figureComparator the comparator used to order the matches for selection
	 */
	public IndexedFigureLocator(int index, IFigureMatcher matcher, Comparator figureComparator) {
		super(new IndexedFigureLoctatorDelegate(index, matcher, figureComparator));
	}
	


}
