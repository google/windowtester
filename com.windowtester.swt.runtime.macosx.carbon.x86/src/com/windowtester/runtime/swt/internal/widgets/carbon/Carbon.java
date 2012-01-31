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
package com.windowtester.runtime.swt.internal.widgets.carbon;

import com.windowtester.swt.macosx.external.OSExt;

public class Carbon {

	public static final OSExt EXTENSIONS = new OSExt();

	private static final String MAC_ACCESSIBILITY_ENABLE_TEXT =
		"Window Tester for Macintosh requires accessibility options to be enabled. Please " +
		"go to System Preferences and open the Universal Access preferences pane. " +
		"Select \"Enable access for assistive devices\" to enable it. You must " +
		"be a system administrator to do this.";
	
	// TODO: push accessibility testing up to a common mac plugin
	
	/**
	 * Return true if the accessibility API must be enabled. This is
	 * Mac-specific so return false if not on a Mac.
	 * 
	 * @return true if running on Mac and accessibility needs to be enabled
	 */
	private static boolean isAccessibilityDisabled() {
//			if (!com.windowtester.internal.runtime.Platform.isRunning())
//				throw new IllegalStateException("Mac WindowTester Tests must be run as JUnit Plug-in Tests");
		return !EXTENSIONS.isAXAPIEnabled();
	}
	
	
	/**
	 * Assert that Mac accessibility is enabled.
	 */
	public static void assertAccessibilityEnabled() {
		if (isAccessibilityDisabled())
			throw new IllegalStateException(MAC_ACCESSIBILITY_ENABLE_TEXT);
	}
	
	
}
