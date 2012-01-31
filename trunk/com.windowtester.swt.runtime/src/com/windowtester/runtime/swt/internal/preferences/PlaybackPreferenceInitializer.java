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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.windowtester.runtime.swt.internal.RuntimePlugin;

/**
 * Class used to initialize default runtime preference values.
 */
public class PlaybackPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = RuntimePlugin.getDefault()
				.getPreferenceStore();
		initializePlaybackPreferences(store);
	}

	/**
	 * Initialize playback default values.
	 */
	private void initializePlaybackPreferences(IPreferenceStore store) {
		
		PlaybackSettings settings = PlaybackSettings.getDefault();
		store.setDefault(PlaybackSettings.DELAY_ON, settings.getDelayOn());
		store.setDefault(PlaybackSettings.HIGHLIGHT_ON, settings.getHighlightingOn());
		store.setDefault(PlaybackSettings.HIGHLIGHT_DURATION, settings.getHighlightDuration());
		store.setDefault(PlaybackSettings.KEY_CLICK_DELAY, settings.getKeyClickDelay());
		store.setDefault(PlaybackSettings.WIDGET_CLICK_DELAY, settings.getWidgetClickDelay());
		store.setDefault(PlaybackSettings.HIGHLIGHT_COLOR, settings.getProperty(PlaybackSettings.HIGHLIGHT_COLOR));
		store.setDefault(PlaybackSettings.MOUSE_MOVE_DELAY, settings.getProperty(PlaybackSettings.MOUSE_MOVE_DELAY));
		store.setDefault(PlaybackSettings.MOUSE_BUTTONS_REMAPPED, settings.getProperty(PlaybackSettings.MOUSE_MOVE_DELAY));
		store.setDefault(PlaybackSettings.EXPERIMENTAL_RUNTIME, settings.getProperty(PlaybackSettings.EXPERIMENTAL_RUNTIME));
		store.setDefault(PlaybackSettings.HIGHLIGHT_SHOW_NOTES, settings.getProperty(PlaybackSettings.HIGHLIGHT_SHOW_NOTES));
	}

}
