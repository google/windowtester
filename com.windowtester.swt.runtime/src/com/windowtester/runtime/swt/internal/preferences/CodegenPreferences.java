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

import com.windowtester.runtime.swt.internal.RuntimePlugin;


public class CodegenPreferences {


	public static final class Defaults {
		//safe default TODO: update with a smart option that is compiler compliance aware
		public static final String STATIC_IMPORT_POLICY = STATIC_IMPORTS_NEVER;
	}

	public static final String STATIC_IMPORT_POLICY = "static_import_policy";

	public static final String STATIC_IMPORTS_ALWAYS = "static_import_always";
	public static final String STATIC_IMPORTS_NEVER = "static_import_never";
	public static final String STATIC_IMPORTS_PROJECT_DEFAULTS = "static_import_project_defaults";


	public static CodegenPreferences stored() {
		return new CodegenPreferences(getStore());
	}

	private final IPreferenceStore store;
		
	public CodegenPreferences(IPreferenceStore store) {
		this.store = store;
	}
		
	
	public String getStaticImportPolicy() {
		return getString(STATIC_IMPORT_POLICY);
	}

	private String getString(String key) {
		return store.getString(key);
	}

	public static IPreferenceStore getStore() {
		return RuntimePlugin.getDefault().getPreferenceStore();
	}


	public boolean usingStatics() {
		String policy = getStaticImportPolicy();
		if (policy == null)
			return false;
		return STATIC_IMPORTS_ALWAYS.equals(policy);
	}



	
	
}
