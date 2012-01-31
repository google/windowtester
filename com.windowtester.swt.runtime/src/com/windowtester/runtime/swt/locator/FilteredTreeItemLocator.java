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

import org.eclipse.ui.dialogs.FilteredTree;

import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.finder.FilteredTreeHelper;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link FilteredTree} items.
 * @see TreeItemLocator
 */
public class FilteredTreeItemLocator extends TreeItemLocator {

	private static final long serialVersionUID = 4261588403734779428L;

	/*
	 * TODO: refactor super so that we don't need to duplicate all of this
	 * construction logic here!
	 */
	
	/** 
	 * Create a locator instance for the common case where no information is needed
	 * to disambiguate the parent control.
	 * <p>
	 * This convenience constructor is equivalent to the following:
	 * <pre>
	 * new FilteredTreeItemLocator(itemText, new FilteredTreeLocator());
	 * </pre>
	 * 
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public FilteredTreeItemLocator(String fullPath) {
		super(fullPath, new FilteredTreeLocator());
	}
	
	/**
	 * Create a locator instance.
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public FilteredTreeItemLocator(String fullPath, SWTWidgetLocator parent) {
		this(fullPath);
		if (parent instanceof ViewLocator)
			parent = new FilteredTreeLocator(parent);
		if (parent == null)
			parent = new FilteredTreeLocator();
		setParentInfo(parent);
	}
		
	//child
	/**
	 * Create a locator instance.
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index the index relative to the parent locator
	 * @param parent the parent locator
	 */
	public FilteredTreeItemLocator(String fullPath, IWidgetLocator parent) {
		super(fullPath);
		if (parent == null)
			setParentInfo(new FilteredTreeLocator());
		setPath(fullPath);
	}

	//indexed child
	/**
	 * Create a locator instance.
	 * @param fullPath the full path to the tree item to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index the index relative to the parent locator
	 * @param parent the parent locator
	 */
	public FilteredTreeItemLocator(String fullPath, int index, IWidgetLocator parent) {
		super(fullPath, index, parent);
		if (parent == null)
			setParentInfo(new FilteredTreeLocator());
		setPath(fullPath);
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.TreeItemLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
		final ISWTWidgetMatcher pathMatcher = super.buildMatcher();
		return new ISWTWidgetMatcher() {
			public boolean matches(ISWTWidgetReference<?> widget) {
				return FilteredTreeHelper.isItemInFilteredTree(widget.getWidget()) && pathMatcher.matches(widget);
			}
		};	
	}
	
	
}
