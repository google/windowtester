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
 * A semantic event that corresponds to an underlying drop event.
 */
public class SemanticDropEvent extends UISemanticEvent {


	private static final long serialVersionUID = -5326282382470899104L;

	/** The drop target selection event */
	private IUISemanticEvent _dropTargetEvent;

	private UISemanticEvent dragSource;
	
	/**
	 * Create an instance.
	 * @param event the selection event underlying the drop
	 */
	public SemanticDropEvent(IUISemanticEvent event) {
		super(getInfo(event));
		_dropTargetEvent = event;
	}
	
	/**
	 * Get the drop target selection event.
	 * @return the drop target selection event
	 */
	public IUISemanticEvent getDropTargetEvent() {
		return _dropTargetEvent;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + " @(" + getX() + "," + getY() + ")";
	}
	
	
	/**
	 * Get info from the given event.
	 * @param event the event in question
	 * @return a corresponding <code>EventInfo</code>
	 */
	private static EventInfo getInfo(IUISemanticEvent event) {
		EventInfo info = new EventInfo();
		info.toString = "Drop Event: " + event.toString();
		info.cls = event.getItemClass();
		info.hierarchyInfo = event.getHierarchyInfo();
		info.button = event.getButton();
		info.x = event.getX();
		info.y = event.getY();
		
		return info;
	}


	public UISemanticEvent withSource(UISemanticEvent dragSource) {
		this.dragSource = dragSource;
		return this;
	}
	
	public UISemanticEvent getDragSource() {
		return dragSource;
	}
	
}
