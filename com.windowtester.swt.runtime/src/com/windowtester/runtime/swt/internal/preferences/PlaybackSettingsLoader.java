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

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * A loader for loading settings.
 *
 */
public class PlaybackSettingsLoader {

	public static void loadFromDisk(IPreferenceStore store) {
		PlaybackSettings settings = PlaybackSettings.loadFromFile();
		store.setValue(PlaybackSettings.DELAY_ON, settings.getDelayOn());
		store.setValue(PlaybackSettings.HIGHLIGHT_ON, settings.getHighlightingOn());
		store.setValue(PlaybackSettings.HIGHLIGHT_DURATION, settings.getHighlightDuration());
		store.setValue(PlaybackSettings.KEY_CLICK_DELAY, settings.getKeyClickDelay());
		store.setValue(PlaybackSettings.WIDGET_CLICK_DELAY, settings.getWidgetClickDelay());
		store.setValue(PlaybackSettings.HIGHLIGHT_COLOR, settings.getPropertyOrDefault(PlaybackSettings.HIGHLIGHT_COLOR, PlaybackSettings.DEFAULT_RGB_STRING));
		store.setValue(PlaybackSettings.MOUSE_MOVE_DELAY, settings.getIntPropertyOrDefault(PlaybackSettings.MOUSE_MOVE_DELAY, PlaybackSettings.DEFAULT_MOUSE_DELAY));
		store.setValue(PlaybackSettings.MOUSE_BUTTONS_REMAPPED, settings.getPropertyOrDefault(PlaybackSettings.MOUSE_BUTTONS_REMAPPED, PlaybackSettings.DEFAULT_MOUSE_REMAP));
		//store.setValue(PlaybackSettings.EXPERIMENTAL_RUNTIME, settings.getProperty(PlaybackSettings.EXPERIMENTAL_RUNTIME));
		//store.setValue(PlaybackSettings.HIGHLIGHT_SHOW_NOTES, settings.getProperty(PlaybackSettings.HIGHLIGHT_SHOW_NOTES));
	}

}
