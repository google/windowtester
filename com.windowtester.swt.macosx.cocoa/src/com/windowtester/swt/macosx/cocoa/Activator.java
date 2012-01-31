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
package com.windowtester.swt.macosx.cocoa;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.swt.internal.abbot.SWTWorkarounds;
import com.windowtester.swt.platform.ext.macosx.MacExtensions;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.windowtester.swt.macosx.cocoa";

	// The shared instance
	private static Activator plugin;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		loadMacExtensions();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	private void loadMacExtensions()  {
		//64 bit cocoa support has been disabled in this branch in favor of pursuing it for W2.
		//NOTE: to find MacCocoa64, look in the "disabled" directory
		//MacExtensions cocoaSupport = OS.is64BitCocoa() ? new MacCocoa64() : new MacCocoa32();
		MacExtensions cocoaSupport = new MacCocoa32();
		SWTWorkarounds.MacExt = cocoaSupport;
	} 


	
}
