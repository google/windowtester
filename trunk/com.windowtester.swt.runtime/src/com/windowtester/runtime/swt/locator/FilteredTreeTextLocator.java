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

import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;

import com.windowtester.runtime.swt.internal.matchers.FilteredTreeTextMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;

/**
 * Locates the {@link Text} widget in a {@link FilteredTree}.
 */
public class FilteredTreeTextLocator extends TextLocator {


	private static final long serialVersionUID = 7650200888142514659L;

	/**
	 * Create a locator instance.
	 */
	public FilteredTreeTextLocator() {
		super();
	}
	
	//child
	/**
	 * Create a locator instance.
	 * @param parent the parent locator	 
	 */
	public FilteredTreeTextLocator(SWTWidgetLocator parent) {
		super(parent);
	}

	//indexed child
	/**
	 * Create a locator instance.
	 * @param index the index with respect to the parent
	 * @param parent the parent locator	 
	 */
	public FilteredTreeTextLocator(int index, SWTWidgetLocator parent) {
		super(index, parent);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
		return new FilteredTreeTextMatcher();
	}
	
}
