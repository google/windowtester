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
package com.windowtester.recorder.event;

import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;


/**
 * Semantic event listeners are notified of Semantic events. 
 */
public interface ISemanticEventListener {

	/**
	 * Notifies this listener that a semantic event has happened.
	 * @param event - the semantic event
	 */
	void notify(IUISemanticEvent event);
  
	////////////////////////////////////////////////////////////////////////////
	//
	// Meta events
	//
	////////////////////////////////////////////////////////////////////////////

	//TODO: maybe these meta-notifications should not have there own methods?
	
    /**
     * Notifies this listener that event recording has started.
     */
    void notifyStart();
    
    /**
     * Notifies this listener that event recording has stopped.
     */
    void notifyStop();

    /**
     * Notifies this listener that the event stream is to be written.
     */
    void notifyWrite();
    
    /**
     * Notifies this listener that root display has been disposed (effectively, recording is terminated).
     */ 
    void notifyDispose();

	/**
	 * Notifies this listener that the event stream is to be flushed and restarted.
	 */
	void notifyRestart();

	/**
	 * Notifies this listener that the event stream is to be paused.
	 */
	void notifyPause();
	
	/**
	 * Notifies this listener that an error occured during recording.
	 * @param event - the error event
	 */
	void notifyError(RecorderErrorEvent event);

	/**
	 * Notifies this listener that a trace event was sent during recording.
	 * @param event - the trace event
	 */
	void notifyTrace(RecorderTraceEvent event);

	/**
	 * Notifies this listener that a hook added vent was sent during recording.
	 * @param hookName 
	 */
	void notifyAssertionHookAdded(String hookName);
	
	/**
	 * Notifies that Recorder Controller was started and listens on specific port 
	 * @param port the port number that this controller started listen on
	 */
	public void notifyControllerStart(int port);
	
	/**
	 * Notifies this listener that Display instance was not found in the application process
	 */
	public void notifyDisplayNotFound();

	/**
	 * Notifies the listener that spy mode has been toggled.
	 */
	void notifySpyModeToggle();
}
