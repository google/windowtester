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
package com.windowtester.runtime.swt.locator;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;

import com.windowtester.runtime.swt.internal.matchers.FilteredTreeMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;

/**
 * Locates {@link FilteredTree} widgets.
 */
public class FilteredTreeLocator extends SWTWidgetLocator {

	
	private static final long serialVersionUID = 6796276531578678877L;

	
	
	/**
	 * Create a locator instance.
	 * @param index the index with respect to the parent
	 * @param parent the parent locator
	 */
	public FilteredTreeLocator(int index, SWTWidgetLocator parent) {
		//notice Tree here -- this is just a placeholder since we do the matching ourselves
		super(Tree.class, index, parent);
	}

	/**
	 * Create a locator instance.
	 * @param index the index with respect to the parent
	 * @param parent the parent locator	 
	 */
	public FilteredTreeLocator(SWTWidgetLocator parent) {
		super(Tree.class, parent);
	}
	
	/**
	 * Create a locator instance.
	 */
	public FilteredTreeLocator() {
		super(Tree.class);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
		return new FilteredTreeMatcher();
	}
	
	
}
