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
package com.windowtester.runtime.swt.internal.operation.effects;

import com.windowtester.internal.runtime.Platform;
import com.windowtester.runtime.swt.internal.RuntimePlugin;
import com.windowtester.runtime.swt.internal.Timer;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;

/**
 * Manages playback delay.
 */
public class PlaybackAdvisor {

	private static final PlaybackAdvisor INSTANCE = new PlaybackAdvisor();
	
	public static PlaybackAdvisor getDefault() {
		return INSTANCE;
	}

	private /* final */ PlaybackSettings settings;
	
	private PlaybackAdvisor() {
		initializeSettings();
	}

	private void initializeSettings() {
		try {
			if (Platform.isRunning())
				settings = RuntimePlugin.getDefault().getPlaybackSettings();
		} catch (Throwable t) {
			//ignore: if an exception occurs we will properly setup settings below
			//TODO: this is NOT a clean way to do this! wee _should_ clean it up!
		}
		if (settings == null) {
			settings = PlaybackSettings.loadFromFile();
		}
	}
	
	/**
	 * Perform the pause specified by {@link PlaybackSettings#getWidgetClickDelay()}.
	 */
	public void postClickPause() {
		if (settings.getDelayOn())
			pause(settings.getWidgetClickDelay());
	}

	private void pause(int delay) {
		new Timer().pause(delay);
	}
	
	
}
