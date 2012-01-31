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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.runtime.swt.internal.preferences.LabelFieldEditor;


/**
 * WT UI preferences page.
 */
public class WTUIPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

//	private final class IndentedBooleanFieldEditor extends BooleanFieldEditor {
//		private IndentedBooleanFieldEditor(String name, String label, Composite parent) {
//			super(name, label, parent);
//		}
//
//		//we do this override to force an indent (there's got to be a better way!)
//		protected void doFillIntoGrid(Composite parent, int numColumns) {
//			super.doFillIntoGrid(parent, numColumns);
//			indentControl(getChangeControl(parent));
//		}
//
//		private void indentControl(Button control) {
//			if (control == null)
//				return;
//			Object layoutData = control.getLayoutData();
//			if (!(layoutData instanceof GridData))
//				return;
//			GridData gridData = (GridData)layoutData;
//			gridData.horizontalIndent = 20;
//		}
//	}


	private static final String USE_CLASSIC_RECORDER_MSG = "&Classic Recorder";
	private static final String USE_CONSOLE_MSG = "&Recording Console";
	
	private static final String RECORDER_MODE_GROUP_LABEL = "Recorder UI Mode";
	
	
	private RadioGroupFieldEditor recordingType;

	public WTUIPreferencePage() {
		super(GRID);
		setPreferenceStore(UiPlugin.getDefault().getPreferenceStore());
		setDescription("WindowTester UI Preferences");
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
	
		recordingType = new RadioGroupFieldEditor(WTUIPreferenceConstants.P_RECORDER_MODE, RECORDER_MODE_GROUP_LABEL, 2, 
				new String[][]{
				{USE_CONSOLE_MSG, WTUIPreferenceConstants.P_RECORDER_MODE_CONSOLE},
				{USE_CLASSIC_RECORDER_MSG, WTUIPreferenceConstants.P_RECORDER_MODE_CLASSIC}
				}, getFieldEditorParent(), true) {
					protected void fireValueChanged(String property,
							Object oldValue, Object newValue) {
						super.fireValueChanged(property, oldValue, newValue);
					}
		};
		
		
		//inspectorEnabled = new BooleanFieldEditor(WTUIPreferenceConstants.P_INSPECTOR_ENABLED, ENABLE_INSPECTOR_MSG, getFieldEditorParent());

		addField(recordingType);
		addField(new LabelFieldEditor("" /*spacer */, getFieldEditorParent()));
		
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
	}
			
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

		
}