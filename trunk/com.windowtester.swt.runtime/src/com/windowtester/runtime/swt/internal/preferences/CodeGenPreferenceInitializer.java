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

/**
 * Class used to initialize default codegen preference values.
 * 
 */
public class CodeGenPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CodegenPreferences.getStore();
		initializePreferences(store);
	}

	/**
	 * Initialize codegen default values.
	 */
	private void initializePreferences(IPreferenceStore store) {
		
		PlaybackSettings settings = PlaybackSettings.getDefault();
		store.setDefault(PlaybackSettings.RUNTIME_VERSION, settings.getRuntimeAPIVersion());
		
		store.setDefault(CodegenPreferences.STATIC_IMPORT_POLICY, CodegenPreferences.Defaults.STATIC_IMPORT_POLICY);
		
	}

}
