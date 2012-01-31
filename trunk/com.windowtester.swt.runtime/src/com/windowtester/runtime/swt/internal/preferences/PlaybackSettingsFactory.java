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

import com.windowtester.runtime.swt.internal.RuntimePlugin;

/**
 * A factory service for playback settings.
 *
 */
public class PlaybackSettingsFactory {

	
	
	public static PlaybackSettings getPlaybackSettings() {
		return com.windowtester.internal.runtime.Platform.isRunning() ? RuntimePlugin.getDefault().getPlaybackSettings() : PlaybackSettings.loadFromFile();
	}
}
