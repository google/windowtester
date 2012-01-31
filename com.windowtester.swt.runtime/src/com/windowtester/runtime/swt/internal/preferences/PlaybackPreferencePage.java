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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.windowtester.internal.debug.Logger;
import com.windowtester.runtime.swt.internal.RuntimePlugin;

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

public class PlaybackPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	
	//private BooleanFieldEditor _experimantalRuntimeChooser;
	private ColorFieldEditor   _colorChooser;
	private IntegerFieldEditor _keystrokeDelayChooser;
	private BooleanFieldEditor _highlightOnChooser;
	private BooleanFieldEditor _delayChooser;
	private IntegerFieldEditor _clickDelayChooser;
	private IntegerFieldEditor _highlightDurationChooser;
	//private BooleanFieldEditor _showNotesAutomatically;
	private IntegerFieldEditor _mouseDelayChooser;
	private BooleanFieldEditor _mouseButtonsRemappedChooser;
	
	public PlaybackPreferencePage() {
		super(GRID);
		setPreferenceStore(RuntimePlugin.getDefault().getPreferenceStore());
		setDescription("WindowTester Playback Preferences");
	}
	
	


	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		
		
//		IPreferenceStore store = getPreferenceStore();
//		Location configurationLocation = Platform.getConfigurationLocation();
//		IPath stateLocation = RuntimePlugin.getDefault().getStateLocation();
//		
		
		
		_delayChooser  = new BooleanFieldEditor(
				PlaybackSettings.DELAY_ON,
				"&Delay",
				getFieldEditorParent()) {
				protected void fireStateChanged(String property, boolean oldValue, boolean newValue) {
					super.fireStateChanged(property, oldValue, newValue);
					//if (oldValue != newValue) {
						_keystrokeDelayChooser.setEnabled(newValue, getFieldEditorParent());
						_clickDelayChooser.setEnabled(newValue, getFieldEditorParent());
						_mouseDelayChooser.setEnabled(newValue, getFieldEditorParent());
					//}
						
				}
		};
		addField(_delayChooser);		
				
		_keystrokeDelayChooser = new IntegerFieldEditor(PlaybackSettings.KEY_CLICK_DELAY, 
				"&Delay between key strokes (in milliseconds)",
				getFieldEditorParent());
		_keystrokeDelayChooser.setEnabled(getDelayOn(), getFieldEditorParent());
		addField(_keystrokeDelayChooser);
		
		
		_clickDelayChooser = new IntegerFieldEditor(PlaybackSettings.WIDGET_CLICK_DELAY, 
				"&Delay after widget clicks (in milliseconds)",
				getFieldEditorParent());
		_clickDelayChooser.setEnabled(getDelayOn(), getFieldEditorParent());
		addField(_clickDelayChooser);
		
		_mouseDelayChooser = new IntegerFieldEditor(PlaybackSettings.MOUSE_MOVE_DELAY, 
				"&Delay between mouse move increments (in milliseconds)",
				getFieldEditorParent());
		_mouseDelayChooser.setEnabled(getDelayOn(), getFieldEditorParent());
		addField(_mouseDelayChooser);
		
		
		
		_highlightOnChooser = new BooleanFieldEditor(
				PlaybackSettings.HIGHLIGHT_ON,
				"&Highlighting",
				getFieldEditorParent()) {
				protected void fireStateChanged(String property, boolean oldValue, boolean newValue) {
					super.fireStateChanged(property, oldValue, newValue);
					//if (oldValue != newValue)
					_colorChooser.setEnabled(newValue, getFieldEditorParent());
					_highlightDurationChooser.setEnabled(newValue, getFieldEditorParent());
					//_showNotesAutomatically.setEnabled(newValue, getFieldEditorParent());
				}
		};
		//_highlightOnChooser.setEnabled(getHighlightOn(), getFieldEditorParent());
		addField(_highlightOnChooser);
		
		
		_highlightDurationChooser = new IntegerFieldEditor(PlaybackSettings.HIGHLIGHT_DURATION, 
				"&Highlight duration (in milliseconds)",
				getFieldEditorParent());
		_highlightDurationChooser.setEnabled(getHighlightOn(), getFieldEditorParent());
		addField(_highlightDurationChooser);
		

		_colorChooser = new ColorFieldEditor(PlaybackSettings.HIGHLIGHT_COLOR, 
				"Highlight color",
				getFieldEditorParent());
		_colorChooser.setEnabled(getHighlightOn(), getFieldEditorParent());
		addField(_colorChooser);

//		_showNotesAutomatically = new BooleanFieldEditor(PlaybackSettings.HIGHLIGHT_SHOW_NOTES,
//				"Show notes automatically", 
//				BooleanFieldEditor.SEPARATE_LABEL,
//				getFieldEditorParent());
//		_showNotesAutomatically.setEnabled(getHighlightOn(), getFieldEditorParent());
//		addField(_showNotesAutomatically);

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		
		_mouseButtonsRemappedChooser = new BooleanFieldEditor(
				PlaybackSettings.MOUSE_BUTTONS_REMAPPED,
				"&Switch primary and secondary mouse buttons in playback",
				getFieldEditorParent());
		addField(_mouseButtonsRemappedChooser);
		
		addField(new LabelFieldEditor("Select this to make the button on the right the one used for primary functions", getFieldEditorParent()));
		addField(new LabelFieldEditor("such as selecting and the left button the one used for secondary functions such", getFieldEditorParent()));
		addField(new LabelFieldEditor("as opening context menus.", getFieldEditorParent()));

		//_experimantalRuntimeChooser = new BooleanFieldEditor(PlaybackSettings.EXPERIMENTAL_RUNTIME, "Use experimental playback runtime", getFieldEditorParent());
		//addField(_experimantalRuntimeChooser);

	}
	
	private boolean getDelayOn() {
		return getPreferenceStore().getBoolean(PlaybackSettings.DELAY_ON);
	}

	private boolean getHighlightOn() {
		return getPreferenceStore().getBoolean(PlaybackSettings.HIGHLIGHT_ON);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		//this is a hack...  I'd expect this to already be done?
		getPreferenceStore().addPropertyChangeListener(RuntimePlugin.getDefault().getPlaybackSettings());
		//TODO: need we remove this?
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	public boolean performOk() {
		doApplySettings();
		return super.performOk();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	protected void performApply() {
		doApplySettings();
		super.performApply();
	}
	

	private void doApplySettings() {
		//TODO: this is a hack ---- the preference story broke when 
		//we started to refactor for WT Server and needs to be fixed
		//this is a temporary stop-gap
		
		
		PlaybackSettings settings = RuntimePlugin.getDefault().getPlaybackSettings();
		settings.loadFromWorkspace(); //set them to the values in the preference page
		
		//and push out
		try {
			RuntimePlugin.getDefault().getPlaybackSettings().store();
		} catch (Throwable th) {
			Logger.log(th);
		}
	}
	
	
}