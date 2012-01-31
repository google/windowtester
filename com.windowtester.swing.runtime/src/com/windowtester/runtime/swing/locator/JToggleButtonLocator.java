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

import javax.swing.JToggleButton;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.condition.IsSelected;
import com.windowtester.runtime.condition.IsSelectedCondition;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * A locator for JToggleButtons.
 */
public class JToggleButtonLocator extends SwingWidgetLocator 
	implements HasText, IsEnabled, IsSelected {

	private static final long serialVersionUID = 6685889878580688192L;

	/**
	 * Create an instance of a locator for a JToggleButton
	 * @param nameOrLabel the text/name of the button
	 */
	public JToggleButtonLocator(String nameOrLabel) {
		super(JToggleButton.class, nameOrLabel);
	}
	
	/**
	 * Create an instance of a locator for a JToggleButton
	 * @param nameOrLabel the text/name of the button
	 * @param parent the locator for the parent of the button
	 */
	public JToggleButtonLocator(String nameOrLabel, SwingWidgetLocator parent) {
		super(JToggleButton.class, nameOrLabel, parent);
	}
	
	/**
	 * Create an instance of a locator for a JToggleButton
	 * @param nameOrLabel the text/name of the button
	 * @param index the index realtive to the parent
	 * @param parent the locator for the parent of the button
	 */
	public JToggleButtonLocator(String nameOrLabel, int index, SwingWidgetLocator parent) {
		super(JToggleButton.class, nameOrLabel, index, parent);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swing.SWingWidgetLocator#getWidgetText(java.awt.Component)
	 */
	protected String getWidgetText(Component widget) {
		return ((JToggleButton)widget).getText();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsSelected#isSelected(com.windowtester.runtime.IUIContext)
	 */
	public boolean isSelected(IUIContext ui) throws WidgetSearchException {
		JToggleButton button = (JToggleButton) ((IWidgetReference)ui.find(this)).getWidget();
		return button.isSelected();
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
	
	
	/**
	 * Create a condition that tests if the given button is selected.
	 * Note that this is a convenience method, equivalent to:
	 * <code>isSelected(true)</code>
	 */
	public IUICondition isSelected() {
		return isSelected(true);
	}
	
	/**
	 * Create a condition that tests if the given button is selected.
	 * @param selected 
	 * @param expected <code>true</code> if the button is expected to be selected, else
	 *            <code>false</code>
	 */            
	public IUICondition isSelected(boolean expected) {
		return new IsSelectedCondition(this, expected);
	}
	
	
	
}
