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

import javax.swing.JMenuItem;

import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsEnabled;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.locator.IMenuItemLocator;
import com.windowtester.runtime.swing.SwingWidgetLocator;

/**
 * A locator for JMenuItems.
 */
public class JMenuItemLocator extends AbstractPathLocator 
	implements IMenuItemLocator, IsEnabled {

	private static final long serialVersionUID = -5514291727454535792L;

	/**
	 * Creates an instance of a JMenuItem locator with the menu path
	 * indicated by a string such as "File/New/Project" 
	 * @param path a String that specifies the complete path to the JMenuItem
	 */
	public JMenuItemLocator(String path) {
		this(path,null);
	}
	
	/**
	 * Creates an instance of a JMenuItem locator with the menu path
	 * indicated by a string such as "File/New/Project" ,relative to a parent
	 * @param path a String that specifies the complete path to the JMenuItem
	 * @param parent locator for the parent
	 */
	public JMenuItemLocator(String path, SwingWidgetLocator parent) {
		this(JMenuItem.class, path, parent);
	}
	
	/**
	 * Creates an instance of a JMenuItem locator with the menu path
	 * indicated by a string such as "File/New/Project" 
	 * @param cls the exact Class of the menu item
	 * @param path a String that specifies the complete path to the JMenuItem
	 */
	public JMenuItemLocator(Class cls,String path){
		this(cls,path,null);
	}

	/**
	 * Creates an instance of a JMenuItem locator with the menu path
	 * indicated by a string such as "File/New/Project", relative to the parent 
	 * @param cls the exact Class of the menu item
	 * @param path a String that specifies the complete path to the JMenuItem
	 * @param parent locator of the parent
	 */
	public JMenuItemLocator(Class cls,String path,SwingWidgetLocator parent){
		super(cls,path,parent);
	}
	
	protected String getWidgetLocatorStringName() {
		return "JMenuItemLocator";
	}

	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swing.locator.AbstractPathLocator#doClick(com.windowtester.runtime2.IUIContext2, int, java.awt.Component, java.awt.Point, int)
	 */
	protected Component doClick(IUIContext ui, int clicks, Component c, Point offset, int modifierMask) {
		((UIContextSwing)ui).getDriver().clickMenuItem((JMenuItem)c);
		return c; 
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
	
	
	
	
}
