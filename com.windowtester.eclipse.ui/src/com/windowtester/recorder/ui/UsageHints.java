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
package com.windowtester.recorder.ui;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.eclipse.ui.preferences.WTUIPreferenceConstants;

/**
 * A service for provider user hints.
 *
 */
public class UsageHints {

	public static void openStartRecordingHintDialog() {
		optionallyDisplayRecorderHintDialog();
	}
	
	private static void optionallyDisplayRecorderHintDialog() {
		final IPreferenceStore store = UiPlugin.getDefault().getPreferenceStore();
		final String key = WTUIPreferenceConstants.P_SHOW_USAGE_INFO;
		String value = store.getString(key);
		
		if (!MessageDialogWithToggle.PROMPT.equals(value))
			return;
		
		final String message = "To begin recording events, click the record button in the \"Recorder Console\" view.";
		final String toggleMessage = "Don't show this message again.";
		final String dialogTitle = "New Recording Session Started";
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialogWithToggle.openInformation(Display.getDefault().getActiveShell(), dialogTitle, message, toggleMessage, false, store, key);				
			}
		});
	}
	
}
