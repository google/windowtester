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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.windowtester.runtime.swt.internal.RuntimePlugin;


/**
 * Codegen pref page.
 */
public class CodegenPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	
	private RadioGroupFieldEditor staticImportPolicy;



	public CodegenPreferencePage() {
		super(GRID);
		setPreferenceStore(RuntimePlugin.getDefault().getPreferenceStore());
		setDescription("WindowTester Code Generation Preferences");
	}


	public void createFieldEditors() {
		
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		

		staticImportPolicy = new RadioGroupFieldEditor(
				CodegenPreferences.STATIC_IMPORT_POLICY, "Static Imports", 1,
				new String[][] { { "Use Static Imports where Possible", CodegenPreferences.STATIC_IMPORTS_ALWAYS },
						{ "Never Use Static Imports", CodegenPreferences.STATIC_IMPORTS_NEVER  } }, getFieldEditorParent());
		addField(staticImportPolicy);	
	}
	


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	

	
	
}