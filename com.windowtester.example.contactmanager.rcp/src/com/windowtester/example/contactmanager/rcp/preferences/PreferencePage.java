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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


import com.swtdesigner.preference.ComboFieldEditor;
import com.windowtester.example.contactmanager.rcp.ContactManagerRCPPlugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	
	private ComboFieldEditor namePref;
	private StringFieldEditor phonePrefixPref;
	
	public PreferencePage() {
		super(GRID);
		setPreferenceStore(ContactManagerRCPPlugin.getDefault().getPreferenceStore());
		setDescription("Preference page for Contacts Manager");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		
		String [][] nameValue = {{"First name displayed before last name","0"},
								 {"Last name displayed before first name","1"}};
		
		
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		
		namePref = new ComboFieldEditor(
			PreferenceConstants.CONTACTS_DISPLAY_BY__FIRST_NAME,
			"Display of name in the Contacts view",
			nameValue,getFieldEditorParent());		
		phonePrefixPref = new StringFieldEditor(
			PreferenceConstants.CONTACTS_DEFAULT_PHONE_AREA_CODE, 
			"Default area code for phone numbers:", 
			getFieldEditorParent());
		
		addField(namePref);
		addField( new SpacerFieldEditor(getFieldEditorParent()));
		addField(phonePrefixPref);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}