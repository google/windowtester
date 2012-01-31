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
package com.windowtester.runtime.gef.locator;


import org.eclipse.draw2d.Figure;

import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.internal.locator.DelegatingLocator;
import com.windowtester.runtime.gef.internal.locator.XYComparator;
import com.windowtester.runtime.gef.internal.locator.provisional.api.IndexedFigureLocator;
import com.windowtester.runtime.gef.internal.matchers.MatcherAdapter;

/**
 * Locates {@link Figure} references by specifying a left-to-right index. 
 * If a locator is specified in  the constructor and, during playback, 
 * this locator describes a list of figures, the index <em>n</em> is used 
 * to select the <em>n</em>th figure in the matched list ordered by their 
 * spatial position left-to-right.
 */
public class LRLocator extends DelegatingLocator {

	private static final long serialVersionUID = -7412618613014381364L;

	private final int index;
	private final FigureLocator locator;

	/**
	 * Construct a new locator specifying an index.
	 * @param index The left-to-right index.
	 * @param locator A locator to whose left-to-right indexed match will be located by this locator.
	 */
	public LRLocator(int index, FigureLocator locator) {
		super(new IndexedFigureLocator(index, adaptToMatcher(locator), new XYComparator()));
		this.index = index;
		this.locator = locator;
		
	}
		
	/**
	 * Get the left-to-right scan index of this locator. 
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Get the indexed locator.
	 */
	public FigureLocator getLocator() {
		return locator;
	}
	
	private static IFigureMatcher adaptToMatcher(FigureLocator locator) {
		return new MatcherAdapter(locator);
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LRLocator(" +  index + ", " + locator + ")";
	}
	
}
