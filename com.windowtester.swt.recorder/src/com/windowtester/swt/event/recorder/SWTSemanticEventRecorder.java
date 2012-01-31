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
package com.windowtester.swt.event.recorder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.windowtester.recorder.IEventFilter;


/**
 * A recorder that records significant SWT events.
 * <p>
 * 08.16.2007:: modified to use mouseMove events to force a buffer flush.  
 * (This greatly improves the interactive recording experience.)
 */
public class SWTSemanticEventRecorder extends BaseEventRecorder {

	/** A list of events in which we are NOT interested */
	private static int[] FILTERED_EVENT_TYPES = /*{};*/
		  { 
			/*SWT.MouseEnter, */ SWT.MouseExit, 
			/* SWT.MouseMove, */ //mouse moves are used to flush the buffer
			SWT.Paint, 
            /*SWT.Activate,*/ 
            /* SWT.Deactivate, */ 
            //SWT.MouseHover, //<--- used by inspector
			/*SWT.FocusIn, SWT.FocusOut,*/
			/*SWT.Dispose,*/
			/*SWT.Move, SWT.Resize,*/
			/*SWT.KeyUp,*/ 
			SWT.Modify, SWT.Verify,
			/* SWT.Show, SWT.Hide, */ 
			/*SWT.MouseUp, SWT.MouseDown, */
			SWT.Traverse, SWT.MenuDetect
		  };
	
	/**
	 * Create an instance.
	 * @param display
	 */
	public SWTSemanticEventRecorder(Display display) {
		super(display);
		//add a filter to exclude various raw SWT events
		addEventFilter(new IEventFilter() {
			public boolean include(Object o) {
				Event event = (Event)o;
				return !isFiltered(event.type);
			}
		});
	}

	/**
	 * Check whether the given event type is filtered OUT of the list of interesting events
	 * @return true if the event type is filtered out
	 */
	public boolean isFiltered(int type) {
		for (int i = 0; i < FILTERED_EVENT_TYPES.length; i++) {
			if (FILTERED_EVENT_TYPES[i] == type)
				return true;
		}
		return false;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.recorder.BaseEventRecorder#logEvent(org.eclipse.swt.widgets.Event)
	 */
	protected void logEvent(Event event) {
		//intercept the mouse move and use it to flush...
		if (event.type == SWT.MouseMove) 
			flushEventBuffer();
		else
			super.logEvent(event);
	} 
	
	

}

