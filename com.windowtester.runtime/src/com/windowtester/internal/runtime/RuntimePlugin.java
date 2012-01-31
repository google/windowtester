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
package com.windowtester.internal.runtime;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.runtime.preferences.PlaybackSettings;

/**
 * The runtime plugin.
 */
public class RuntimePlugin extends Plugin {
	
	/** The unique identifier for this plugin */
	public static final String PLUGIN_ID = "com.windowtester.runtime";	
	
	/** A settings object for playback */
	private PlaybackSettings _settings;
	
	
	/** The shared instance. */
	private static RuntimePlugin _plugin;
	
	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin   = this;
		//set the platform flag to true indicating that we are running in the context of the platform
		Platform.IS_RUNNING = true;
	}
	
	
	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
	
	
	/**
	 * Returns the singleton instance of the runtime plug-in.
	 */
	public static RuntimePlugin getDefault() {
		return _plugin;
	}
	
	/**
	 * Get the String identifier for this plugin.
	 * @return the plugin id
	 */
	public static String getPluginId() {
		return PLUGIN_ID;
	}
	
	public PlaybackSettings getPlaybackSettings() {
		if (_settings == null) {
			TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "getting settings from file");
			_settings = PlaybackSettings.loadFromFile();
//			_settings.flushToPreferenceStore(getPreferenceStore());
		}
		return _settings;
	}

}
