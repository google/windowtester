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
package com.windowtester.recorder.ui.remote.standalone;

/**
 * Presenter for the remote.
 */
class RemotePresenter {

	private final RecorderGateway recorder;
	private final HookNameRequester requester = new HookNameRequester();

	private RemoteActionFactory actions;
	
	static RemotePresenter forRecorder(RecorderGateway recorder) {
		return new RemotePresenter(recorder);
	}
	
	RemotePresenter withActions(RemoteActionFactory actions) {
		this.actions = actions;
		setInitialActionStates();
		return this;
	}
	
	private void setInitialActionStates() {
		actions.PAUSE.disable();
		actions.RECORD.enable();
		actions.SPY.disable();
	}

	private RemotePresenter(RecorderGateway recorder) {
		this.recorder = recorder;
	}
	
	public void record() {
		actions.RECORD.disable();
		actions.SPY.enable();
		recorder.startRecorder();
	}

	public void hook() {
		String hookName = requester.getNameFromUser();
		if (hookName != null)
			recorder.addAssertion(hookName);	
	}

	public void pause() {
		actions.PAUSE.disable();
		actions.SPY.disable();
		recorder.pauseRecorder();
	}

	public void spy() {
		actions.SPY.disable();
		recorder.toggleSpyMode();
	}
	
	
}
