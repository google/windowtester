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
package com.windowtester.swt.event.server;

import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.meta.RecorderMetaEvent;
import com.windowtester.recorder.event.user.SemanticEventHandler;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;

/**
 * A controller class that will recieve meta events from developer Workbench 
 * and will delegate them to GUI Recorder
 */
public class SessionEventController extends SemanticEventServer {

	public SessionEventController(String name) {
		super(name);
		setHandler(new RecorderMetaEventHandler());
	}
	
	/**
	 * A special event handler that handles the meta events.
	 */
	class RecorderMetaEventHandler extends SemanticEventHandler {

		public void handlePause(RecorderMetaEvent event) {
			EventRecorderPlugin.pauseRecording();
		}

		public void handle(RecorderAssertionHookAddedEvent event) {
			EventRecorderPlugin.addAssertion(event.getHookName());
		}

		public void handleRestart(RecorderMetaEvent event) {
			EventRecorderPlugin.restartRecording();
		}

		public void handleStart(RecorderMetaEvent event) {
			EventRecorderPlugin.startRecording();
		}

		public void handleStop(RecorderMetaEvent event) {
			EventRecorderPlugin.terminateRecording();
		}
		
		public void handleSpyModeToggled(RecorderMetaEvent recorderMetaEvent) {
			EventRecorderPlugin.toggleSpyMode();
		}
	}
}
