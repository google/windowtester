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
package com.windowtester.eclipse.ui.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.eclipse.ui.usage.ProfiledAction;

/**
 * Action to open the WT preferences page.
 */
public class OpenWTPreferencesPageAction extends ProfiledAction {

	private final IShellProvider shellProvider;

	public OpenWTPreferencesPageAction(IShellProvider shellProvider) {
		super("Preferences...");
		this.shellProvider = shellProvider;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.usage.ProfiledAction#doRun()
	 */
	public void doRun() {
		Dialog dialog = org.eclipse.ui.dialogs.PreferencesUtil
				.createPreferenceDialogOn(
						getShell(),
						"com.windowtester.swt.runtime.preferences.GeneralPreferencePage",
						null, null);
		if (dialog != null)
			dialog.open();
	}

	private Shell getShell() {
		return shellProvider.getShell();
	}

}
