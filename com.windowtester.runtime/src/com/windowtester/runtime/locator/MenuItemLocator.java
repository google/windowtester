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
package com.windowtester.runtime.locator;

/**
 * A basic menu item locator for use in locating items in context clicks (for example).
 */
public class MenuItemLocator implements IMenuItemLocator {

	private final String _path;

	public MenuItemLocator(String path) {
		_path = path;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime2.locator.IMenuItemLocator#getPath()
	 */
	public String getPath() {
		return _path;
	}

}
