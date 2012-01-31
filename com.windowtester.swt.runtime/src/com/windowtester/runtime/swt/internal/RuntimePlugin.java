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
package com.windowtester.runtime.swt.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.BundleContext;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.swt.internal.preferences.ColorManager;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettingsLoader;
import com.windowtester.runtime.swt.internal.preferences.PreferencesAdapter;

/**
 * The SWT Runtime plugin.
 * 
 */
public class RuntimePlugin extends Plugin
{
	/** The unique identifier for this plugin */
	public static final String PLUGIN_ID = "com.windowtester.swt.runtime";	
	
	/** The shared instance. */
	private static RuntimePlugin _plugin;
	
    //TODO: Should this field be moved into the PlaybackSettings class
    // along with its accessor method?
	/** A settings object for playback */
	private PlaybackSettings _settings;

	//TODO: this needs to be fixed --- this field should be removed
	// along with the associated method... see TODO by method
	private IPreferenceStore preferenceStore;

	/**
	 * Returns the singleton instance of the runtime plug-in.
	 */
	public static RuntimePlugin getDefault() {
		return _plugin;
	}
	
	//================================================================================
	// Bundle Lifecycle

	/**
	 * Extend superclass implementation to allow the platform specific plugin
	 * to be initialized and to be added to this plugin's classpath 
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin   = this;
		
		//have the playback settings listen for preference change events
		//		getPreferenceStore().addPropertyChangeListener(getPlaybackSettings());
	}

	public void stop(BundleContext context) throws Exception {
		if (preferenceStore != null)
			savePluginPreferences();
		super.stop(context);
		//clean up any allocated highlight colors
		ColorManager.getDefault().dispose();
	}
	
	//================================================================================
	// Accessors
	
	//TODO: this needs to be fixed --- this adapter should be replaced
	//by a store that is backed by our .properties file on disk
    public IPreferenceStore getPreferenceStore() {
        if (preferenceStore == null) {
			preferenceStore = new PreferencesAdapter(getPluginPreferences());
			PlaybackSettingsLoader.loadFromDisk(preferenceStore);
        }
        return preferenceStore;
    }
	
    //TODO: Should this method be moved into the PlaybackSettings class
    // as a new #getGlobalSettings() method?
    /**
     * Answer the global playback settings.
     * @return the settings (not <code>null</code>)
     */
	public PlaybackSettings getPlaybackSettings() {
		if (_settings == null) {
			TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "getting settings from file");
			_settings = PlaybackSettings.loadFromFile();
			//_settings.flushToPreferenceStore(getPreferenceStore());
		}
		return _settings;
	}
	

}
