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
package com.windowtester.runtime.swing.locator;

import java.awt.Component;

import javax.swing.JButton;

import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.util.StringComparator;


/**
 * A locator for JButtons.
 */
public class JButtonLocator extends SwingWidgetLocator 
 	implements HasText, HasFocus, IsEnabled {

	private static final long serialVersionUID = 3445424001537844748L;

	/**
	 * Create an instance of a locator that locates a JButton by its text or name
	 * @param label the text/name of the JButton
	 */
	public JButtonLocator(String label) {
		super(JButton.class, label);
	}

		
	/**
	 * Create an instance of a locator that locates a JButton by its text or name,
	 * relative to the given parent
	 * @param label the text/name of the JButton
	 * @param parent the locator for the parent component
	 */
	public JButtonLocator(String label, SwingWidgetLocator parent) {
		super(JButton.class, label, parent);
	}
	
		
	/**
	 * Create an instance of a locator that locates a JButton by its text or name,
	 * relative to the given parent.
	 * @param label the text/name of the JButton
	 * @param index the index relative to it's parent
	 * @param parent the locator for the parent component
	 */
	public JButtonLocator(String label, int index, SwingWidgetLocator parent) {
		super(JButton.class, label, index, parent);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swing.SWingWidgetLocator#getWidgetText(java.awt.Component)
	 */
	protected String getWidgetText(Component widget) {
		return ((JButton)widget).getText();
	}
	
	protected String getWidgetLocatorStringName() {
		return "JButtonLocator";
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	
	/**
	 * Create a condition that tests if the given widget has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
	
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
	
	
	
}
