/*******************************************************************************
 *
 *   Copyright (c) 2012 Google, Inc.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *   
 *   Contributors:
 *   Google, Inc. - initial API and implementation
 *  
 *******************************************************************************/
 
package com.windowtester.example.contactmanager.rcp.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.windowtester.example.contactmanager.rcp.ContactManagerRCPPlugin;


/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = ContactManagerRCPPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(PreferenceConstants.CONTACTS_DISPLAY_BY__FIRST_NAME, "1");
		store.setDefault(PreferenceConstants.CONTACTS_DEFAULT_PHONE_AREA_CODE,
				"951");
	}

}
