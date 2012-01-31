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
import java.awt.Point;

import javax.swing.JComboBox;

import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.HasFocus;
import com.windowtester.runtime.condition.HasFocusCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swing.SwingWidgetLocator;


/**
 * A locator for JComboBoxes.
 */
public class JComboBoxLocator extends AbstractPathLocator implements IsEnabled, HasFocus{

	private static final long serialVersionUID = -1258270010418601192L;

	
	/**
	 * Creates a locator for a JComboBox with the specified selection
	 * @param itemText the selected item
	 */
	public JComboBoxLocator(String itemText) {
		this(itemText,(SwingWidgetLocator)null);
	}
	
	/**
	 * Creates a locator for a JComboBox, can be used to locate an editable
	 * combo box.
	 * @param parentInfo the locator for the parent
	 */
	public JComboBoxLocator(SwingWidgetLocator parentInfo){
		this (null,parentInfo);
	}

	/**
	 * Creates a locator for a JComboBox with the specified selection, and 
	 * parent locator.
	 * @param itemText the selected item
	 * @param parentInfo locator for the parent
	 */
	public JComboBoxLocator(String itemText, SwingWidgetLocator parentInfo) {
		this(itemText, UNASSIGNED, parentInfo);
	}
	
	/**
	 * Creates an locator for a JComboBox with the sepcified selection, parent 
	 * locator and it's relative index in the parent
	 * @param itemText the selected item
	 * @param index the relative index
	 * @param parentInfo locator for the parent
	 */
	public JComboBoxLocator(String itemText, int index, SwingWidgetLocator parentInfo) {
		this(JComboBox.class, itemText, index, parentInfo);
	}

	/**
	 * Creates an locator for a JComboBox with the sepcified selection, parent 
	 * locator and it's relative index in the parent
	 * @param cls the exact class of the component
	 * @param itemText the selected item
	 * @param index the relative index
	 * @param parentInfo locator for the parent
	 */
	public JComboBoxLocator(Class cls,String itemText, int index, SwingWidgetLocator parentInfo) {
		super(cls,itemText,index,parentInfo);
	}
	
	protected Component doClick(IUIContext ui, int clicks, Component c, Point offset, int modifierMask) {
		return ((UIContextSwing)ui).getDriver().clickComboBox((JComboBox)c, getItemText(),clicks);
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
