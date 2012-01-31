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
package com.windowtester.eclipse.ui.target;

/**
 * Constants for target building.
 */
public interface RequiredPlugins {
	
	public static String[] RECORDING = new String[]{
		"com.windowtester.runtime",
		"com.windowtester.swt.runtime",  
        "com.windowtester.swing.runtime",
        "com.windowtester.swt.recorder",
        "com.windowtester.swing.recorder"
	};
	
	//notice that this lists ALL platform-specific plugins
	//the way that they are resolved at runtime ensures that only ones loaded in the IDE get resolved
	//meaning that only plugins appropriate for the target OS will be included
	public static String[] RUNTIME = new String[]{
		"com.windowtester.runtime",
		"com.windowtester.swt.runtime",  
        "com.windowtester.swing.runtime",
		"com.windowtester.swt.runtime.linux.gtk.x86",
		"com.windowtester.swt.runtime.win32.win32.x86",
		"com.windowtester.swt.runtime.win32.win32.x86_64",
		"com.windowtester.swt.runtime.macosx.carbon.x86",
		"com.windowtester.swt.runtime.macosx.cocoa.x86", 
		"com.windowtester.swt.runtime.macosx.cocoa.x86_64"
	};	
	
	public static String[] RUNTIME_DEPENDENCIES = new String[]{
        "org.junit",
        "org.eclipse.core.resources",
        "org.eclipse.ant.core",
        "org.eclipse.core.variables",
        "org.eclipse.core.filesystem",
        "org.eclipse.ui.forms",
        "org.eclipse.jdt.core",
        "org.eclipse.text",
        "org.eclipse.team.core"
	};	
	
	public static String[] GEF_SUPPORT = new String[]{
		"com.windowtester.swt.runtime.gef"
	};
	
}
