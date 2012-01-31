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
package com.windowtester.eclipse.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;

import com.windowtester.eclipse.ui.UiPlugin;

/**
 * Initializes default preference values.
 */
public class WTUIPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = UiPlugin.getDefault().getPreferenceStore();
		store.setDefault(WTUIPreferenceConstants.P_SHOW_USAGE_INFO, MessageDialogWithToggle.PROMPT);
		store.setDefault(WTUIPreferenceConstants.P_RECORDER_MODE, WTUIPreferenceConstants.P_RECORDER_MODE_CONSOLE);
	}

}
