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

import com.windowtester.recorder.event.IUISemanticEvent;


/**
 * A semantic event that corresponds to an underlying drag event.
 */
public class SemanticDragEvent extends UISemanticEvent {

	private static final long serialVersionUID = -8693716018145122731L;
	
	/** The drag source selection event */
	private IUISemanticEvent _dragSourceEvent;
	
	
	/**
	 * Create an instance.
	 * @param event the selection event underlying the drag start
	 */
	public SemanticDragEvent(IUISemanticEvent event) {
		super(getInfo(event));
		_dragSourceEvent = event;
	}

	
	/**
	 * Get the drag source selection event.
	 * @return the drag source selection event
	 */
	public IUISemanticEvent getDragSourceEvent() {
		return _dragSourceEvent;
	}
	
	
	
	/**
	 * Get info from the given event.
	 * @param event the event in question
	 * @return a corresponding <code>EventInfo</code>
	 */
	private static EventInfo getInfo(IUISemanticEvent event) {
		EventInfo info = new EventInfo();
		info.toString = "Drag Event: " + event.toString();
		info.cls = event.getItemClass();
		info.hierarchyInfo = event.getHierarchyInfo();
		info.button = event.getButton();
		info.x = event.getX();
		info.y = event.getY();
		
		return info;
	}






}
