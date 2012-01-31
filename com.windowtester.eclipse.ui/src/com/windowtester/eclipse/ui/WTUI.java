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
package com.windowtester.eclipse.ui;

import org.eclipse.jface.preference.IPreferenceStore;

import com.windowtester.eclipse.ui.preferences.WTUIPreferenceConstants;

/**
 * Facade for access to common services and configuration details.
 */
public class WTUI {

	
	public static boolean isInspectorEnabled() {
		return true; //this is now the default
		//return getPreferences().getBoolean(WTUIPreferenceConstants.P_INSPECTOR_ENABLED);
	}

	public static boolean isRecorderConsoleViewEnabled() {
		return !isClassicRecorderModeEnabled();
	}

	public static boolean isClassicRecorderModeEnabled() {
		return getPreferences().getString(WTUIPreferenceConstants.P_RECORDER_MODE).equals(WTUIPreferenceConstants.P_RECORDER_MODE_CLASSIC);
	}

	private static IPreferenceStore getPreferences() {
		return UiPlugin.getDefault().getPreferenceStore();
	}
	
}
