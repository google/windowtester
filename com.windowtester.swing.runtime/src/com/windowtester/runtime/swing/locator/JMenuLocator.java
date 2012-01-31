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

import javax.swing.JMenu;

import com.windowtester.runtime.swing.SwingWidgetLocator;

/**
 * A locator for JMenus.
 */
public class JMenuLocator extends JMenuItemLocator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1369029475541129631L;

	/**
	 * Creates an instance of a JMenuLocator
	 * @param path a String that specifies the complete path to the JMenu
	 */
	public JMenuLocator(String path) {
		this(path, null);
	}
	
	/**
	 * Creates an instance of a JMenuLocator
	 * @param path a String that specifies the complete path to the JMenu
	 * @param parent locator of the parent
	 */
	public JMenuLocator(String path, SwingWidgetLocator parent) {
		super(JMenu.class, path, parent);
	}
	
	
	protected String getWidgetLocatorStringName() {
		return "JMenuLocator";
	}
	
	
}
