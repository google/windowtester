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

import javax.swing.JCheckBoxMenuItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsSelected;
import com.windowtester.runtime.condition.IsSelectedCondition;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swing.SwingWidgetLocator;

/**
 * A locator for JCheckBoxMenuItems.
 */
public class JCheckBoxMenuItemLocator extends JMenuItemLocator  implements IsSelected {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4036431358452057857L;

	/**
	 * Creates an instance of a JCheckBoxMenuItem locator with the menu path
	 * indicated by a string such as "File/New/Project" 
	 * @param path a String that specifies the complete path to the JCheckBoxMenuItem
	 */
	public JCheckBoxMenuItemLocator(String path) {
		this(path,null);
	}
	
	/**
	 * Creates an instance of a JCheckBoxMenuItem locator with the menu path
	 * indicated by a string such as "File/New/Project", relative to its parent 
	 * @param path a String that specifies the complete path to the JCheckBoxMenuItem
	 * @param parent the locator for the parent of the menu item
	 */
	public JCheckBoxMenuItemLocator(String path, SwingWidgetLocator parent) {
		super(JCheckBoxMenuItem.class, path, parent);
	}
	
	protected String getWidgetLocatorStringName() {
		return "JCheckBoxMenuItemLocator";
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsSelected#isSelected(com.windowtester.runtime.IUIContext)
	 */
	public boolean isSelected(IUIContext ui) throws WidgetSearchException {
		JCheckBoxMenuItem item = (JCheckBoxMenuItem) ((IWidgetReference)ui.find(this)).getWidget();
		return item.isSelected();
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

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
