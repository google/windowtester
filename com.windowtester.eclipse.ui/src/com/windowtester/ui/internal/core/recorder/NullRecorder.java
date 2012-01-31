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
package com.windowtester.ui.internal.core.recorder;

import com.windowtester.recorder.IEventFilter;
import com.windowtester.recorder.IEventRecorder;
import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;

public class NullRecorder implements IEventRecorder {

	public void addEventFilter(IEventFilter filter) {
		//no-op

	}

	public void addHook(String hookName) {
		//no-op

	}

	public void addListener(ISemanticEventListener listener) {
		//no-op

	}

	public void pause() {
		//no-op

	}

	public void record(IUISemanticEvent semanticEvent) {
		//no-op

	}

	public void removeEventFilter(IEventFilter filter) {
		//no-op

	}

	public void removeListener(ISemanticEventListener listener) {
		//no-op

	}

	public void reportError(RecorderErrorEvent event) {
		//no-op

	}

	public void restart() {
		//no-op

	}

	public void start() {
		//no-op

	}

	public void stop() {
		//no-op

	}

	public void terminate() {
		//no-op

	}

	public void trace(RecorderTraceEvent event) {
		//no-op

	}

	public void write() {
		//no-op

	}
	
	public void toggleSpyMode() {
		//no-op
	}

	public boolean isRecording() {
		return false;
	}
	
}
