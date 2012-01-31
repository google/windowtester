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
package com.windowtester.recorder.event.user;

import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;

/**
 * This adapter class provides default implementations for the
 * methods described by the <code>ISemanticEventListener</code> interface.
 **/
public class SemanticEventAdapter implements ISemanticEventListener {

	public void notify(IUISemanticEvent event) {
		//no-op
		
	}

	public void notifyStart() {
		//no-op
		
	}

	public void notifyStop() {
		//no-op
		
	}

	public void notifyWrite() {
		//no-op
		
	}

	public void notifyDispose() {
		//no-op
		
	}

	public void notifyRestart() {
		//no-op
		
	}

	public void notifyPause() {
		//no-op
		
	}

	public void notifyError(RecorderErrorEvent event) {
		//no-op
		
	}

	public void notifyTrace(RecorderTraceEvent event) {
		//no-op
		
	}

	public void notifyAssertionHookAdded(String hookName) {
		//no-op
		
	}

	public void notifyControllerStart(int port) {
		//no-op
		
	}

	public void notifyDisplayNotFound() {
		//no-op
		
	}

	public void notifySpyModeToggle() {
		//no-op
			
	}
}
