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

import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.meta.RecorderControllerStartEvent;
import com.windowtester.recorder.event.meta.RecorderDisplayNotFoundEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderMetaEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticMenuSelectionEvent;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent;

/**
 * A "visitor" for handling ISemanticEvents.
 * 
 */
public interface ISemanticEventHandler {

    /**
     * Handles the given SemanticKeyDownEvent event.
     * @param event - the event to handle
     */
    void handle(SemanticKeyDownEvent event);

    /**
     * Handles the given SemanticMenuSelectionEvent event.
     * @param event - the event to handle
     */
    void handle(SemanticMenuSelectionEvent event);

    /**
     * Handles the given SemanticWidgetSelectionEvent event.
     * @param event - the event to handle
     */
    void handle(SemanticWidgetSelectionEvent event);

    /**
     * Handles the given SemanticEvent event.
     * @param event - the event to handle
     */
    void handle(UISemanticEvent event);    
    
    /**
     * Handles the given SemanticTreeItemSelectionEvent event.
     * @param event - the event to handle
     */
    void handle(SemanticTreeItemSelectionEvent event);    
    
    /**
     * Handles Recorder disposal.
     * @param event - the dispose event
     */
    void handleDispose(RecorderMetaEvent event);    
    
    /**
     * Handles Recorder start.
     * @param event - the start event
     */
    void handleStart(RecorderMetaEvent event);    
    
    /**
     * Handles Recorder stop.
     * @param event - the stop event
     */
    void handleStop(RecorderMetaEvent event);    
    
    void handlePause(RecorderMetaEvent event);
    
    /**
     * Handles Recorder restart.
     * @param event - the restart event
     */
    void handleRestart(RecorderMetaEvent event);

	/**
     * Handles internal Recorder errors.
     * @param event - the error event
	 */
	void handleError(RecorderErrorEvent event);

	/**
     * Handles Recorder debugging trace events.
     * @param event - the trace event
	 */
	void handleTrace(RecorderTraceEvent event);

	/**
	 * Handles Recorder assertion events.
	 * @param event - the assertion event
	 */
	void handle(RecorderAssertionHookAddedEvent event);
	
	/**
	 * Handles the start of the main meta event controller  
	 * @param event the controller start meta event
	 */
	void handleControllerStart(RecorderControllerStartEvent event);

	/**
	 * Handles the event when Display instance was not found
	 * @param event Display not found event
	 */
	void handleDisplayNotFound(RecorderDisplayNotFoundEvent event);

	
	/**
	 * If supported, toggle spy recording mode.
	 * @param recorderMetaEvent 
	 */
	void handleSpyModeToggled(RecorderMetaEvent recorderMetaEvent);
	
    
	void handleInspectionEvent(SemanticWidgetInspectionEvent inspectionEvent);
	
}
