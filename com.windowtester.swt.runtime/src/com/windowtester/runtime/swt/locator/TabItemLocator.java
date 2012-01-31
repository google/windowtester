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

import org.eclipse.swt.widgets.TabItem;

import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link TabItem} widgets.
 */
public class TabItemLocator extends SWTWidgetLocator {
	
	private static final long serialVersionUID = 2464452061853145190L;

	/** 
	 * Create a locator instance for the common case where no information is needed
	 * to disambiguate the parent control.
	 * <p>
	 * This convenience constructor is equivalent to the following:
	 * <pre>
	 * new TabItemLocator(itemText, new SWTWidgetLocator(TabFolder.class));
	 * </pre>
	 * 
	 * @param text the text of the Tab to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public TabItemLocator(String itemText) {
		super(TabItem.class, itemText);
	}

	//child
	/**
	 * Create a locator instance.
	 * @param text the text of the Tab to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator
	 */
	public TabItemLocator(String text, SWTWidgetLocator parent) {
		super(TabItem.class, text, parent);
	}

	//indexed child
	/**
	 * Create a locator instance.
	 * @param text the text of the Tab to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator
	 */
	public TabItemLocator(String text, int index, SWTWidgetLocator parent) {
		super(TabItem.class, text, index, parent);
	}
	
}
