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

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.ofClass;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.visible;

import org.eclipse.swt.widgets.ToolBar;

import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swt.internal.matchers.SWTMatcherBuilder;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;

/**
 * Locates {@link ToolBar} widgets.
 */
public class ToolBarLocator  extends SWTWidgetLocator
	implements IsEnabled, HasFocus
{
	private static final long serialVersionUID = 8194661177031756245L;

	
	private SWTMatcherBuilder matcherBuilder = new SWTMatcherBuilder();
	
	
	/**
	 * Create a locator instance.
	 */
	public ToolBarLocator() {
		super(ToolBar.class);
	}

	//child
	/**
	 * Create a locator instance.
	 * @param parent the parent locator	 
	 */
	public ToolBarLocator(SWTWidgetLocator parent) {
		super(ToolBar.class, parent);
	}

	//indexed child
	/**
	 * Create a locator instance.
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator	 
	 */
	public ToolBarLocator(int index, SWTWidgetLocator parent) {
		super(ToolBar.class, index, parent);
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	@Override
	protected ISWTWidgetMatcher buildMatcher() {
		matcherBuilder.specify(ofClass(ToolBar.class), visible());
		return matcherBuilder.build();
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Create a condition that tests if the given widget is enabled.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isEnabled(true)</code>
	 */
	public IUICondition isEnabled() {
		return isEnabled(true);
	}
	
	/**
	 * Create a condition that tests if the given widget is enabled.
	 * @param expected <code>true</code> if the text is expected to be enabled, else
	 *            <code>false</code>
	 * @see IsEnabledCondition
	 */            
	public IUICondition isEnabled(boolean expected) {
		return new IsEnabledCondition(this, expected);
	}
	
	public ToolBarLocator in(int index, SWTWidgetLocator parent) {
		ToolBarLocator locator = new ToolBarLocator();
		locator.setParentInfo(parent);
		locator.setIndex(index);
		locator.matcherBuilder.specify(matcherBuilder.criteria());
		locator.matcherBuilder.scope(index, parent.buildMatcher());
		return locator;
	}

//	protected ToolBarLocator createLocator() {
//		return new ToolBarLocator();
//	}
//	
//	@Override
//	public ToolBarLocator in(SWTWidgetLocator parent) {
//		return (ToolBarLocator) in(UNASSIGNED, parent);
//	}
//	
//	@Override
//	public ToolBarLocator in(int index, SWTWidgetLocator parent) {
//		return (ToolBarLocator) super.in(index, parent);
//	}
	
}