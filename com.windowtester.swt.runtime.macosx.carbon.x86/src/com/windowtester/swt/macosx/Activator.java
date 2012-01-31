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
package com.windowtester.swt.macosx;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.windowtester.runtime.swt.internal.widgets.carbon.Carbon;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	/** The unique identifier for this plugin */
	public static final String PLUGIN_ID = "com.windowtester.swt.runtime.carbon.macosx.x86";

	/** The shared instance. */
	private static Activator plugin;

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	//================================================================================
	// Bundle Lifecycle

	/**
	 * Extend superclass implementation to initialize the receiver functionality
	 * by hooking into the base SWT implementation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		Carbon.assertAccessibilityEnabled(); // TODO[pq]: is this the right place for this?
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	
	
}
