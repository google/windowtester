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
 * Base OSX extension functionality. 
 * @see MacExtensions 
 */
public abstract class OSX  {

	private static final String MAC_ACCESSIBILITY_ENABLE_TEXT =
		"Window Tester for Macintosh requires accessibility options to be enabled. Please " +
		"go to System Preferences and open the Universal Access preferences pane. " +
		"Select \"Enable access for assistive devices\" to enable it. You must " +
		"be a system administrator to do this.";
	
	private final MacExtensions extensions;
	
	public OSX(MacExtensions extensions){
		this.extensions = extensions;
	}
		
	/**
	 * Assert that Mac accessibility is enabled.
	 */
	public void assertAccessibilityEnabled() {
		if (!isAXAPIEnabled())
			throw new IllegalStateException(MAC_ACCESSIBILITY_ENABLE_TEXT);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.runtime.internal.macosx.cocoa.MacExtensions#getMenuItemBounds(org.eclipse.swt.widgets.MenuItem)
	 */
	public Rectangle getMenuItemBounds(MenuItem menuItem) {
		return extensions.getMenuItemBounds(menuItem);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.runtime.internal.macosx.cocoa.MacExtensions#getTabItemBounds(org.eclipse.swt.widgets.TabItem)
	 */
	public Rectangle getTabItemBounds(TabItem item) {
		return extensions.getTabItemBounds(item);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.runtime.internal.macosx.cocoa.MacExtensions#isAXAPIEnabled()
	 */
	public boolean isAXAPIEnabled() {
		return extensions.isAXAPIEnabled();
	}
	
	
}
