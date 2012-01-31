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
package com.windowtester.runtime.swt.internal.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;

import com.windowtester.runtime.swt.internal.RuntimePlugin;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;

/**
 * A class that encapsulates settings for playback.  Settings are backed by a file
 * on disk and are kept in sync with the preference store.
 */
public class PlaybackSettings implements IPropertyChangeListener {

	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Preference key constants
	//
	////////////////////////////////////////////////////////////////////////////////

	public static final String DELAY_ON               = "delay.on";
	public static final String KEY_CLICK_DELAY        = "key.click.delay";
	public static final String WIDGET_CLICK_DELAY     = "widget.click.delay";
	public static final String HIGHLIGHT_ON           = "highlight.on";
	public static final String HIGHLIGHT_DURATION     = "highlight.duration";
	public static final String HIGHLIGHT_COLOR        = "highlight.color";
	public static final String MOUSE_MOVE_DELAY       = "mouse.move.delay";
	public static final String MOUSE_BUTTONS_REMAPPED = "mouse.button.remapped";
	public static final String EXPERIMENTAL_RUNTIME   = "experimental.playback.runtime";
	public static final String HIGHLIGHT_SHOW_NOTES   = "highlight.showNotesAutomatically";
	public static final String RUNTIME_VERSION        = "runtime.version";
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Default values
	//
	////////////////////////////////////////////////////////////////////////////////

	public static final int    DEFAULT_CLICK_DELAY        = 1000;
	public static final int    DEFAULT_KEY_CLICK_DELAY    = 200;
	public static final int    DEFAULT_HIGHLIGHT_DURATION = 1000;
	public static final String DEFAULT_RGB_STRING         = "0,128,255";
	public static final int    DEFAULT_MOUSE_DELAY        = 3;
	public static final int    DEFAULT_RUNTIME_VERSION    = 2;
	public static final String DEFAULT_MOUSE_REMAP        = "false";
	
	
	
	//the default location
    private static String _settingsDirPath = System.getProperty("user.home")
            + System.getProperty("file.separator") + "WindowTester";
        
    
    ////////////////////////////////////////////////////////////////////////////////
	//
	// State
	//
	////////////////////////////////////////////////////////////////////////////////

    /** The directory for storing persisted values */
    private File _defaultDir;
    
    /** The underlying file for persisting values */
    private File _settings;
    
    /** The backing properties object */
    private Properties _properties;
    
    /** A set of property change event listeners */
    private List _listeners = new ArrayList();
    
   
    ////////////////////////////////////////////////////////////////////////////////
	//
	// Instance creation
	//
	////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Create an instance.
     */
    PlaybackSettings() {
    
    }
    
    /**
     * Get an instance initialized to all default values.
     */
    public static PlaybackSettings getDefault() {
    	PlaybackSettings def = new PlaybackSettings();
    	def.resetDefaults();
    	return def;
    }
    
    /**
     * Get an instance whose values are based on those stored in the workspace
     * preferences.
     */
    public static PlaybackSettings getFromWorkspace() {
    	PlaybackSettings settings = new PlaybackSettings();
    	settings.loadFromWorkspace();
    	return settings;
    }
    
    /**
     * Get an instance loaded from the backing file in the file system.
     */
	public static PlaybackSettings loadFromFile() {
    	PlaybackSettings settings = new PlaybackSettings();
    	return settings;
	}
	

	/**
	 * Load values from workspace preferences.
	 */
	void loadFromWorkspace() {
		IPreferenceStore store = RuntimePlugin.getDefault().getPreferenceStore();
		setDelayOn(store.getBoolean(DELAY_ON));
		setHiglightingOn(store.getBoolean(HIGHLIGHT_ON));
		setKeyClickDelay(store.getInt(KEY_CLICK_DELAY));
		setWidgetClickDelay(store.getInt(WIDGET_CLICK_DELAY));
		setHighlightColor(store.getString(HIGHLIGHT_COLOR));
		setHighlightDuration(store.getInt(HIGHLIGHT_DURATION));
		setMouseMoveDelay(store.getInt(MOUSE_MOVE_DELAY));
		setMouseButtonsRemapped(store.getBoolean(MOUSE_BUTTONS_REMAPPED));
		setExperimentalPlaybackOn(store.getBoolean(EXPERIMENTAL_RUNTIME));
		setShowNotesOn(store.getBoolean(HIGHLIGHT_SHOW_NOTES));
	}

	public void flushToPreferenceStore(IPreferenceStore store) {
		store.setValue(DELAY_ON, getDelayOn());
		store.setValue(HIGHLIGHT_ON, getHighlightingOn());
		store.setValue(KEY_CLICK_DELAY, getKeyClickDelay());
		store.setValue(WIDGET_CLICK_DELAY, getWidgetClickDelay());
		store.setValue(HIGHLIGHT_COLOR, StringConverter.asString(getHighlightColor()));
		store.setValue(HIGHLIGHT_DURATION, getHighlightDuration());
		store.setValue(MOUSE_MOVE_DELAY, getMouseMoveDelay());
		store.setValue(MOUSE_BUTTONS_REMAPPED, getMouseButtonsRemapped());
		store.setValue(EXPERIMENTAL_RUNTIME, getExperimentalPlaybackOn());
		store.setValue(HIGHLIGHT_SHOW_NOTES, getShowNotesOn());
	}
	
	
	/**
	 * Reset values to their defaults.
	 */
	public void resetDefaults() {
		setHiglightingOn(false);
		setDelayOn(false);
		setKeyClickDelay(DEFAULT_KEY_CLICK_DELAY);
		setWidgetClickDelay(DEFAULT_CLICK_DELAY);
		setHighlightColor(DEFAULT_RGB_STRING);
		setHighlightDuration(DEFAULT_HIGHLIGHT_DURATION);
		setMouseMoveDelay(DEFAULT_MOUSE_DELAY);
		setMouseButtonsRemapped(false);
		setExperimentalPlaybackOn(false);
		setShowNotesOn(false);
	}
	
	
	/**
	 * Fetch the settings file.
	 */
	public File getSettingsFile() {
    	if (_settings == null)	{
    		try {
				_settings = initSettings();
			} catch (IOException e) {
				LogHandler.log(e);
			}
    	}
    	return _settings;
    }
    
	/**
	 * Initialize the settings file.
	 */
    private File initSettings() throws IOException {
        _defaultDir = new File(_settingsDirPath);
        if (!_defaultDir.exists())
            _defaultDir.mkdir();
        _settings = new File(_defaultDir, "settings.properties");
        if (!_settings.exists())
        	_settings.createNewFile();
        return _settings;
	}

	/**
	 * Add a change listener who will be notified when properties are changed.
	 */
	public void addChangeListener(IPlaybackSettingsChangeListener listener) {
		_listeners.add(listener);
	}

	/**
	 * Remove a given change listener from the list of registered listeners.
	 */
	public void removeChangeListener(IPlaybackSettingsChangeListener listener) {
		_listeners.remove(listener);
	}
	
	/**
	 * Notify listeners of a change event.
	 * @param key - the key of the property that changed
	 * @param oldValue - the old value
	 * @param newValue - the new value
	 */
	private void fireSettingChangedEvent(String key, String oldValue, String newValue) {
		for(Iterator iter = _listeners.iterator(); iter.hasNext(); ) {
			((IPlaybackSettingsChangeListener)iter.next()).settingChanged(key, oldValue, newValue);
		}
	}

	/**
	 * Store these settings.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void store() throws FileNotFoundException, IOException {
		TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, this.toString() + " storing");
		Properties props = getProperties();
		
		String propString = props == null ? "null properties" : _properties.toString();
		TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, propString);
		OutputStream os = new FileOutputStream(_settings);
		getProperties().store(os, "");
		os.close();
	}
	
	
    ////////////////////////////////////////////////////////////////////////////////
	//
	// Setting value accessors
	//
	////////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Set the key click delay. 
	 * @param ms - the new delay
	 * @return the old delay value
	 */	
	public int setKeyClickDelay(int ms) {
		return setIntValue(KEY_CLICK_DELAY, ms, DEFAULT_CLICK_DELAY);
	}

	/**
	 * Get the current key click delay.
	 */
	public int getKeyClickDelay() {
		return getIntValue(KEY_CLICK_DELAY,DEFAULT_CLICK_DELAY);
	}

	/**
	 * Get the current widget delay.
	 */
	public int getWidgetClickDelay() {
		return getIntValue(WIDGET_CLICK_DELAY, DEFAULT_CLICK_DELAY);
	}
	
	public int getRuntimeAPIVersion() {
		//we don't want to treat non-present values as loggable events (migration case)
		if (getProperty(RUNTIME_VERSION)== null)
			return DEFAULT_RUNTIME_VERSION;
		return getIntValue(RUNTIME_VERSION, DEFAULT_RUNTIME_VERSION);
	}
	
	public int setRuntimeAPIVersion(int version) {
		return setIntValue(RUNTIME_VERSION, version, DEFAULT_RUNTIME_VERSION);
	}

	
	public int getMouseMoveDelay() {
		return getIntValue(MOUSE_MOVE_DELAY, DEFAULT_MOUSE_DELAY);
	}
	
	public int setMouseMoveDelay(int ms) {
		return setIntValue(MOUSE_MOVE_DELAY, ms, DEFAULT_MOUSE_DELAY);
	}
	
	/**
	 * Set the widget click delay. 
	 * @param ms - the new delay
	 * @return the old delay value
	 */	
	public int setWidgetClickDelay(int ms) {
		return setIntValue(WIDGET_CLICK_DELAY, ms, DEFAULT_CLICK_DELAY);
	}

	/**
	 * Turn delay on or off. 
	 * @param isOn - the new value
	 * @return the old value
	 */	
	public boolean setDelayOn(boolean isOn) {
		return setBooleanValue(DELAY_ON, isOn, false);
	}
	
	/** 
	 * Get the current delay value.
	 */
	public boolean getDelayOn() {
		return getBooleanValue(DELAY_ON, false);
	}
	
	
	/**
	 * Turn button mapping on or off. 
	 * @param isOn - the new value
	 * @return the old value
	 */	
	public boolean setMouseButtonsRemapped(boolean isOn) {
		return setBooleanValue(MOUSE_BUTTONS_REMAPPED, isOn, false);
	}
	
	/** 
	 * Get the button mapping value.
	 */
	public boolean getMouseButtonsRemapped() {
		return getBooleanValue(MOUSE_BUTTONS_REMAPPED, false);
	}
	
	/**
	 * Turn highlighting on or off. 
	 * @param isOn - the new value
	 * @return the old value
	 */	
	public boolean setHiglightingOn(boolean isOn) {
		return setBooleanValue(HIGHLIGHT_ON, isOn, false);
	}
	
	/**
	 * Get the current highlighting duration.
	 * @return the previous duration
	 */
	public int getHighlightDuration() {
		return getIntValue(HIGHLIGHT_DURATION, DEFAULT_HIGHLIGHT_DURATION);
	}
	
	/**
	 * Set the current highlighting duration.
	 */
	public int setHighlightDuration(int ms) {
		return setIntValue(HIGHLIGHT_DURATION, ms, DEFAULT_HIGHLIGHT_DURATION);
	}
	
	/**
	 * Get whether highlighting is on.
	 */
	public boolean getHighlightingOn() {
		return getBooleanValue(HIGHLIGHT_ON, false);
	}
	
	/**
	 * Get the current highlight color 
	 */
	public RGB getHighlightColor() {
		String c = getProperty(HIGHLIGHT_COLOR);
		RGB rgb = StringConverter.asRGB(c, null /*DEFAULT_RGB*/);
		return rgb;
	}
	
	/**
	 * Set the current highlight color.
	 */
	public void setHighlightColor(String rgbString) {
		//first make sure the string is properly formatted...
		RGB rgb = StringConverter.asRGB(rgbString, null);
		//if the value is bad, return early 
		if (rgb == null)
			return;
		//if the value is ok, proceed to set it
		setProperty(HIGHLIGHT_COLOR, rgbString);
	}
	/**
	 * Get the experimental playback runtime flag value
	 * @return true if it is on
	 */
	public boolean getExperimentalPlaybackOn(){
		return getBooleanValue(EXPERIMENTAL_RUNTIME, false);
	}
	
	/**
	 * Set experimental playback runtime value 
	 * @param on the value
	 */
	public void setExperimentalPlaybackOn(boolean on){
		setBooleanValue(EXPERIMENTAL_RUNTIME, on, false);
	}
	
	/**
	 * Get show notes flag value
	 * @return true if it is on
	 */
	public boolean getShowNotesOn(){
		return getBooleanValue(HIGHLIGHT_SHOW_NOTES, false);
	}
	
	/**
	 * Set show notes flag value 
	 * @param on the value
	 */
	public void setShowNotesOn(boolean on){
		setBooleanValue(HIGHLIGHT_SHOW_NOTES, on, false);
	}
	
    ////////////////////////////////////////////////////////////////////////////////
	//
	// Property setting helpers
	//
	////////////////////////////////////////////////////////////////////////////////
	
	   /**
     * Get the backing properties from the settings file.
     */
    public Properties getProperties() {
    	File settings = getSettingsFile();
    	if (settings == null)
    		return null;
    	if (_properties == null) {
    		_properties = new Properties();
    		try {
				_properties.load(new FileInputStream(settings));
				if (_properties.isEmpty())
					resetDefaults();
			} catch (FileNotFoundException e) {
				LogHandler.log(e);
			} catch (IOException e) {
				LogHandler.log(e);
			}
    	}
    	return _properties;
    }


	/**
	 * Get the value of the property with the given value.
	 */
	public String getProperty(String key) {
		return getProperties().getProperty(key);
	}

	/**
	 * Get the value of the property with the given value.
	 */
	public String getPropertyOrDefault(String key, String defaultFallBack) {
		String property = getProperties().getProperty(key);
		if (property == null)
			return defaultFallBack;
		return property;
	}
	
	public int getIntPropertyOrDefault(String key, int defaultFallBack) {
		String property = getProperties().getProperty(key);
		if (property == null)
			return defaultFallBack;
		try {
			return Integer.parseInt(property);
		} catch(Throwable e) {
			return defaultFallBack;
		}
	}
	
	
	/**
	 * Set a property.
	 * @param key - the property key
	 * @param value - the property value
	 * @return the old value
	 */
	public String setProperty(String key, String value) {
		Properties props = getProperties();
		String oldValue = null;
		if (props != null) {
			//System.out.println(this.toString() + " setting property: " + key + " to " + value);
			oldValue = (String) props.setProperty(key, value);
			//store(); //<-- requires a separate call now
		}
		fireSettingChangedEvent(key, oldValue, value);
		return oldValue;
	}
	
	/**
	 * Get this property value as an int, using a default on conversion failure.
	 */
	private int getIntValue(String key, int defaultValue) {
		int value = defaultValue; //ignoring errors for now --- who should handle them?
		String d = getProperty(key);
		try {
			value = Integer.parseInt(d);
		} catch (Throwable e) {
			LogHandler.log(e);
		}
		return value;
	}
	
	/**
	 * Set this property value as an int, using a default on conversion failure.
	 */
	private int setIntValue(String key, int value, int defaultValue) {
		int oldValue = defaultValue; //ignoring errors for now --- who should handle them?
		String v = setProperty(key, new Integer(value).toString());
		try {
			if (v!=null)
				oldValue = Integer.parseInt(v);
		} catch(Throwable e) {
			LogHandler.log(e);
		}
		return oldValue;			
	}
	
	/**
	 * Get this property value as a bool, usuing a default on conversion failure.
	 */
	private boolean getBooleanValue(String key, boolean defaultValue) {
		boolean value = defaultValue; //ignoring errors for now --- who should handle them?
		String d = getProperty(key);
		try {
			value = Boolean.valueOf(d).booleanValue();
		} catch(Throwable e) {
			LogHandler.log(e);
		}
		return value;
	}

	/**
	 * Set this property value as a bool, usuing a default on conversion failure.
	 */
	private boolean setBooleanValue(String key, boolean value, boolean defaultValue) {
		boolean oldValue = defaultValue; //ignoring errors for now --- who should handle them?
		String v = setProperty(key, new Boolean(value).toString());
		try {
			if (v!=null)
				oldValue = Boolean.valueOf(v).booleanValue();
		} catch(Throwable e) {
			LogHandler.log(e);
		}
		return oldValue;			
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {

		String key = event.getProperty();
		Object newValue = event.getNewValue();
		// if we have a property for it (or if it is a newly added one... ugh)
		if (getProperties().containsKey(key) || isNewProperty(key)) {
			String propertyString = makePropertyString(key, newValue);
			setProperty(key, propertyString); // update the property
			try {
				store();
			} catch (FileNotFoundException e) {
				LogHandler.log(e);
			} catch (IOException e) {
				LogHandler.log(e);
			}
		} else {
			TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "property: " + key + " ignored");
		}
			
	}

	/**
	 * A hack to allow for properties that have been added post 1.0.
	 * This is necessary because we use the pre-existing properties to filter out
	 * property events that we don't care about.
	 * TODO: fix this so that the hack is unnecessary
	 */
	private boolean isNewProperty(String key) {
		return key == MOUSE_BUTTONS_REMAPPED || key == RUNTIME_VERSION;
	}

	/**
	 * Make an appropriate property value string from this value.
	 */
	private String makePropertyString(String key, Object newValue) {
		if (key.equals(HIGHLIGHT_COLOR)) {
			if (newValue instanceof String)
				return (String)newValue;
			RGB rgb = (RGB)newValue;
			return rgb.red + "," + rgb.green + "," + rgb.blue;
		}
		return newValue.toString();
	}

	/**
	 * A settings changed listener.
	 */
	public static interface IPlaybackSettingsChangeListener {
		
		/**
		 * The given setting has changed.
		 */
		void settingChanged(String settingKey, Object oldValue, Object newValue);
		
	}



}
