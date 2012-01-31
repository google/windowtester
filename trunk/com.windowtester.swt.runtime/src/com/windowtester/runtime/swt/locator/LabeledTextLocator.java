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

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates labeled {@link Text} widgets.
 * @see LabeledLocator
 *
 */
public class LabeledTextLocator extends LabeledLocator
	implements HasText, HasFocus
{
	private static final long serialVersionUID = 4785056765576752088L;

	/**
	 * Create a locator instance.
	 * @param labelText the text of the label to match
	 */	
	public LabeledTextLocator(String labelText) {
		super(Text.class, labelText);
	}

	/**
	 * Create a locator instance.
	 * @param labelText the text of the label to match
	 * @param index the index relative to the parent locator
	 * @param parent the parent locator
	 */
	public LabeledTextLocator(String labelText, int index, SWTWidgetLocator parentLocator) {
		super(Text.class, labelText, index, parentLocator);
	}

	/**
	 * Create a locator instance.
	 * @param labelText the text of the label to match
	 * @param parent the parent locator
	 */
	public LabeledTextLocator(String labelText, SWTWidgetLocator parentLocator) {
		super(Text.class, labelText, parentLocator);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getWidgetText(org.eclipse.swt.widgets.Control)
	 */
	protected String getWidgetText(Control widget) {
		return ((Text) widget).getText();
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
	 * @param selected 
	 * @param expected <code>true</code> if the menu is expected to be enabled, else
	 *            <code>false</code>
	 * @see IsEnabledCondition
	 */            
	public IUICondition isEnabled(boolean expected) {
		return new IsEnabledCondition(this, expected);
	}
	
	/**
	 * Create a condition that tests if the given widget has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
	
}
