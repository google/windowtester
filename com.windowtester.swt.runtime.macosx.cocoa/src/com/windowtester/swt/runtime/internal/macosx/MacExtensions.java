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
package com.windowtester.swt.runtime.internal.macosx;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;

/**
 * Additional Mac OS X native methods that should be in SWT. These are
 * isolated in an interface, defined in a plug-in with minimal dependencies,
 * in order to prevent cyclic dependencies among plug-ins. The interface
 * is required to allow the platform-specific plugin to not be loaded on
 * other platforms.
 */
public interface MacExtensions {

	/**
	 * Given a MenuItem, return its bounding box.
	 * 
	 * @param item the menu item
	 * @return Rectangle of item (in global coordinates), or null if something didn't work
	 */
	Rectangle getMenuItemBounds(MenuItem menuItem);

	/**
	 * Given a TabItem, return its bounding box.
	 * 
	 * @param item the tab item
	 * @return Rectangle of item (in global coordinates), or null if something didn't work
	 */
	Rectangle getTabItemBounds(TabItem item);

	/**
	 * Return true if the accessibility API is enabled.
	 * <p>
	 * To enable it: open System Preferences, select Universal Access, then
	 * select "Enable access for assistive devices".
	 * @return true if the accessibility API is enabled
	 */
	boolean isAXAPIEnabled();
}
