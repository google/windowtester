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
package com.windowtester.swt.event.model;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import com.windowtester.internal.runtime.MouseConfig;
import com.windowtester.recorder.event.user.UISemanticEvent;

/**
 * Helper for managing drag events.
 */
public class DNDHelper 
/* $codepro.preprocessor.if version >= 3.3 $ */
implements org.eclipse.swt.events.DragDetectListener 
/* $codepro.preprocessor.endif $ */
{

	
	private UISemanticEvent dragSource;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.DragDetectListener#dragDetected(org.eclipse.swt.events.DragDetectEvent)
	 */
	/* $codepro.preprocessor.if version >= 3.3 $ */
	public void dragDetected(org.eclipse.swt.events.DragDetectEvent e) {
		Event event = synthesizeEvent(e);
		dragSource = SWTSemanticEventFactory.createWidgetSelectionEvent(event);
		System.out.println("drag source: " + dragSource);
	}
	/* $codepro.preprocessor.endif $ */
	
	/* $codepro.preprocessor.if version >= 3.3 $ */
	private Event synthesizeEvent(org.eclipse.swt.events.DragDetectEvent e) {
		Event event = new Event();
		event.button = MouseConfig.PRIMARY_BUTTON;
		event.widget = e.widget;
		event.x      = e.x;
		event.y   	 = e.y;
		return event;
	}
	/* $codepro.preprocessor.endif $ */
	
	
	public void startListeningTo(Control control) {
		/* $codepro.preprocessor.if version >= 3.3 $ */
		if (control instanceof Canvas) {
			((Canvas)control).addDragDetectListener(this);		
		}	
		/* $codepro.preprocessor.endif $ */
	}

	public void stopListeningTo(Control control) {
		/* $codepro.preprocessor.if version >= 3.3 $ */
		if (control instanceof Canvas) {
			((Canvas)control).removeDragDetectListener(this);			
		}
		/* $codepro.preprocessor.endif $ */
	}

	
	public void processed() {
		dragSource = null;
	}

	public UISemanticEvent getDragSource() {
		return dragSource;
	}

}
