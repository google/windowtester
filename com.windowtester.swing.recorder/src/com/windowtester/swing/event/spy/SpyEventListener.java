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
package com.windowtester.swing.event.spy;

import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;

public class SpyEventListener implements ISemanticEventListener {

	public void notify(IUISemanticEvent event) {
		// TODO Auto-generated method stub

	}

	public void notifyAssertionHookAdded(String hookName) {
		// TODO Auto-generated method stub

	}

	public void notifyControllerStart(int port) {
		// TODO Auto-generated method stub

	}

	public void notifyDisplayNotFound() {
		// TODO Auto-generated method stub

	}

	public void notifyDispose() {
		// TODO Auto-generated method stub

	}

	public void notifyError(RecorderErrorEvent event) {
		// TODO Auto-generated method stub

	}

	public void notifyPause() {
		// TODO Auto-generated method stub

	}

	public void notifyRestart() {
		// TODO Auto-generated method stub

	}

	public void notifySpyModeToggle() {
		SpyEventHandler.spyModeToggled();
	}

	public void notifyStart() {
		// TODO Auto-generated method stub

	}

	public void notifyStop() {
		// TODO Auto-generated method stub

	}

	public void notifyTrace(RecorderTraceEvent event) {
		// TODO Auto-generated method stub

	}

	public void notifyWrite() {
		// TODO Auto-generated method stub

	}

}
