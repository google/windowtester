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

import com.windowtester.recorder.event.ISemanticEventHandler;
import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.meta.RecorderControllerStartEvent;
import com.windowtester.recorder.event.meta.RecorderDisplayNotFoundEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderMetaEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;

/**
 * A base event handler that ignores all events. 
 *
 */
public class SemanticEventHandler implements ISemanticEventHandler {


	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handle(com.windowtester.recorder.event.user.SemanticKeyDownEvent)
	 */
	public void handle(SemanticKeyDownEvent event) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handle(com.windowtester.recorder.event.user.SemanticMenuSelectionEvent)
	 */
	public void handle(SemanticMenuSelectionEvent event) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handle(com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent)
	 */
	public void handle(SemanticWidgetSelectionEvent event) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handle(com.windowtester.recorder.event.user.UISemanticEvent)
	 */
	public void handle(UISemanticEvent event) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handle(com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent)
	 */
	public void handle(SemanticTreeItemSelectionEvent event) {
		//no-op
	}


	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleDispose(com.windowtester.recorder.event.internal.RecorderMetaEvent)
	 */
	public void handleDispose(RecorderMetaEvent event) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleStart(com.windowtester.recorder.event.internal.RecorderMetaEvent)
	 */
	public void handleStart(RecorderMetaEvent event) {
		//no-op
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleStop(com.windowtester.recorder.event.internal.RecorderMetaEvent)
	 */
	public void handleStop(RecorderMetaEvent event) {
		//no-op
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleRestart(com.windowtester.recorder.event.internal.RecorderMetaEvent)
	 */
	public void handleRestart(RecorderMetaEvent event) {
		//no-op
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleError(com.windowtester.recorder.event.internal.RecorderErrorEvent)
	 */
	public void handleError(RecorderErrorEvent event) {
		//no-op
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleTrace(com.windowtester.recorder.event.internal.RecorderTraceEvent)
	 */
	public void handleTrace(RecorderTraceEvent event) {
		//no-op
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handle(com.windowtester.recorder.event.internal.RecorderAssertionHookAddedEvent)
	 */
	public void handle(RecorderAssertionHookAddedEvent event) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleControllerStart(com.windowtester.recorder.event.internal.RecorderControllerStartEvent)
	 */
	public void handleControllerStart(RecorderControllerStartEvent event) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleDisplayNotFound(com.windowtester.recorder.event.internal.RecorderDisplayNotFoundEvent)
	 */
	public void handleDisplayNotFound(RecorderDisplayNotFoundEvent event) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handlePause(com.windowtester.recorder.event.internal.RecorderMetaEvent)
	 */
	public void handlePause(RecorderMetaEvent event) {
		// no-op
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleSpyModeToggled(com.windowtester.recorder.event.meta.RecorderMetaEvent)
	 */
	public void handleSpyModeToggled(RecorderMetaEvent recorderMetaEvent) {
		// no-op	
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEventHandler#handleInspectionEvent(com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent)
	 */
	public void handleInspectionEvent(SemanticWidgetInspectionEvent inspectionEvent) {
		// no-op	
	}
	
}
