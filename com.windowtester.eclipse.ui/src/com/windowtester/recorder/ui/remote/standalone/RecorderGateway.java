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

import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.meta.RecorderMetaEvent;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;

/**
 * Communicates with the recorder instance.
 */
class RecorderGateway {

	static RecorderGateway forPort(int port) {
		return new RecorderGateway(port);
	}
	
	private final int port;
	
	private RecorderGateway(int recorderPort) {
		this.port = recorderPort;
	}
	
	public void startRecorder() {
		EventRecorderPlugin.send(RecorderMetaEvent.START, port);
	}
	
	public void stopRecorder() {
		EventRecorderPlugin.send(RecorderMetaEvent.STOP, port);
	}
	
	public void pauseRecorder() {
		EventRecorderPlugin.send(RecorderMetaEvent.PAUSE, port);
	}
	
	public void addAssertion(String hookName) {
		EventRecorderPlugin.send(new RecorderAssertionHookAddedEvent(hookName), port);	
	}
	
	public void toggleSpyMode() {
		EventRecorderPlugin.send(RecorderMetaEvent.TOGGLE_SPY, port);
	}
	
	
	
}
