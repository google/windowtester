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

import java.util.ArrayList;
import java.util.List;

import com.windowtester.internal.debug.Tracer;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.meta.RecorderMetaEvent;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;


public class WorkbenchEventController extends SemanticEventServer {
	
	/** The list of cached events */
	private List _events = new ArrayList();
	
	public WorkbenchEventController() {
		super("WorkbenchEventController");
	}
	
	/**
	 * Handle the given event.
	 * @param event - the event to handle
	 */
	protected void handleEvent(ISemanticEvent event) {
		Tracer.trace(EventRecorderPlugin.PLUGIN_ID+"/trace", "Received event: " + toString(event));
		if (event == RecorderMetaEvent.RESTART)
			_events.clear(); //clear events for a fresh start
		
		//store event for later code generation
		if(event instanceof IUISemanticEvent || event instanceof RecorderAssertionHookAddedEvent)
			_events.add(event);
		super.handleEvent(event);
	}
	
	private String toString(ISemanticEvent event) {
		//serialization sometimes breaks toString()
		//in this case, return a generic description
		try {	
			return event.toString();
		} catch(Throwable e) {
			return "Event type: " + event.getClass();
		}
	}

	public List getEvents(){
		return _events;
	}
}
