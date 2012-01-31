package com.windowtester.test.recorder.ui;

import junit.framework.TestCase;

import com.windowtester.recorder.ui.EventSequenceModel;
import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.recorder.ui.IRecorderPanelModel;
import com.windowtester.recorder.ui.RecorderPanelModel;
import com.windowtester.recorder.ui.RecorderUI;
import com.windowtester.recorder.ui.IRecorderPanelModel.IChangeListener;
import com.windowtester.ui.core.model.ISemanticEvent;

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
public class RecorderPanelModelTest extends TestCase {

	
	public void testInitialState() {
		IRecorderPanelModel model = getPanelModel();
		assertInitialState(model);
	}

	private void assertInitialState(IRecorderPanelModel model) {
		assertOutOfSession(model);
		assertRecordDisabled(model);
		assertPauseDisabled(model);
		assertRestartDisabled(model);
		assertHookDisabled(model);
	}

	public void testInitialStateRestoredAfterSessionEnd() {
		IRecorderPanelModel model = getPanelModel();
		model.sessionStarted();
		model.sessionEnded();
		assertInitialState(model);
	}
	
	public void testSessionStartAndStop() {
		IRecorderPanelModel model = getPanelModel();
		model.sessionStarted();
		assertInSession(model);
		model.sessionEnded();
		assertOutOfSession(model);
	}
	
	
	public void testSecondSession() {
		IRecorderPanelModel model = getPanelModel();
		model.sessionStarted();
		assertInSession(model);
		model.sessionEnded();
		assertOutOfSession(model);
		model.sessionStarted();
		assertInSession(model);		
	}
	
	
	public void testRecordButtonEnablement() {
		IRecorderPanelModel model = getPanelModel();
		model.sessionStarted();
		assertRecordEnabled(model);
		model.clickRecord();
		assertRecordDisabled(model);
		assertPauseEnabled(model);
		assertRestartEnabled(model);
	}

	public void testHookButtonEnablement() {
		IRecorderPanelModel model = getPanelModel();
		model.sessionStarted();
		assertHookEnabled(model);
		model.sessionEnded();
		assertHookDisabled(model);
	}
	

	public void testPauseButtonEnablement() {
		IRecorderPanelModel model = getPanelModel();
		model.sessionStarted();
		model.clickRecord();
		model.clickPause();
		assertPauseDisabled(model);
		assertRecordEnabled(model);
	}
	
	public void testRestartButtonEnabledOnlyWhenEventsRecorded() {
		IEventSequenceModel seqModel = new EventSequenceModel();		
		IRecorderPanelModel model = new RecorderPanelModel(seqModel);
		model.sessionStarted();
		assertRestartDisabled(model);
		seqModel.add(new ISemanticEvent(){});
		assertRestartEnabled(model);
	}

	
	public void testNoModelChangesFiredWhenPauseClickedAndSessionIsIdle() {
		IRecorderPanelModel model = getPanelModel();
		model.sessionStarted();
		ChangeListener cl = addChangeListener(model);
		model.clickPause();
		assertFalse(cl.wasChanged());
	}
	
	public void testModelChangesFiredWhenRecordClickedAndSessionIsIdle() {
		IRecorderPanelModel model = getPanelModel();
		model.sessionStarted();
		ChangeListener cl = addChangeListener(model);
		model.clickRecord();
		assertTrue(cl.wasChanged());
	}
	
	
	

	
	
	
	class ChangeListener implements IChangeListener {

		int _changeCount;
		
		public void panelChanged() {
			_changeCount++;
		}
		
		int count() {
			return _changeCount;
		}
		
		boolean wasChanged() {
			return count() > 0;
		}
		
		void reset() {
			_changeCount = 0;
		}
	}
		
	private ChangeListener addChangeListener(IRecorderPanelModel model) {
		ChangeListener listener = new ChangeListener();
		model.addListener(listener);
		return listener;
	}
	
	private void assertRecordDisabled(IRecorderPanelModel model) {
		assertFalse(model.isRecordEnabled());
	}

	private void assertRestartEnabled(IRecorderPanelModel model) {
		assertTrue(model.isRestartEnabled());
	}
	
	private void assertRestartDisabled(IRecorderPanelModel model) {
		assertFalse(model.isRestartEnabled());
	}

	private void assertPauseDisabled(IRecorderPanelModel model) {
		assertFalse(model.isPauseEnabled());
	}
	
	private void assertPauseEnabled(IRecorderPanelModel model) {
		assertTrue(model.isPauseEnabled());
	}

	private void assertRecordEnabled(IRecorderPanelModel model) {
		assertTrue(model.isRecordEnabled());
	}
	
	private IRecorderPanelModel getPanelModel() {
		return RecorderUI.getPanelModel();
	}
	
	private void assertOutOfSession(IRecorderPanelModel model) {
		assertFalse(model.inSession());
	}

	private void assertInSession(IRecorderPanelModel model) {
		assertTrue(model.inSession());
	}
	
	private void assertHookEnabled(IRecorderPanelModel model) {
		assertTrue(model.isHookEnabled());
	}
	
	private void assertHookDisabled(IRecorderPanelModel model) {
		assertFalse(model.isHookEnabled());
	}
	
	
	
}
