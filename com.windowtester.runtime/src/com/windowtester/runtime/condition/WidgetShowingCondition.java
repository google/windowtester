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
package com.windowtester.runtime.condition;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Tests whether a given widget is showing.
 * <p>
 * An example use might be to test whether a wizard page is up by testing for its label:
 * 
 * <pre> 
 *   //wait for the Plug-in Content wizard page to show
 *   ui.wait(new WidgetShowingCondition(new LabelLocator("Plug-in Content")));
 * </pre>
 * 
 */
public class WidgetShowingCondition
	implements ICondition
{
	private final IUIContext ui;
	private final IWidgetLocator locator;

	/**
	 * Create an instance using the default display.
	 * 
	 * @param locator the locator to use to identify the widget in question
	 */
	public WidgetShowingCondition(IUIContext ui, IWidgetLocator locator) {
		this.ui = ui;
		this.locator = locator;
	}

	/**
	 * Test whether the widget in question is showing.
	 * 
	 * @see com.windowtester.swt.condition.ICondition#test()
	 */
	public boolean test() {
		/*
		 * !pq: This matches the original semantics but could use refinement.
		 * If there are multiple matches, should we throw an exception?
		 */
		return ui.findAll(locator).length == 1;
	}

}
