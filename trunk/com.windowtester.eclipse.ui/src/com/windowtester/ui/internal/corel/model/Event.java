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
package com.windowtester.ui.internal.corel.model;

import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.ui.core.model.IEvent;
import com.windowtester.ui.core.model.IEventGroup;

public class Event implements IEvent, IAdaptable {

	private final com.windowtester.recorder.event.ISemanticEvent _event;
	private IEventGroup _group;
	
	public Event(com.windowtester.recorder.event.ISemanticEvent event) {
		_event = event;
	}

	public void setGroup(IEventGroup group) {
		_group = group;
	}
		
	public IEventGroup getGroup() {
		return _group;
	}
	
	public Object getParent(Object o) {
		return _group;
	}

	public String toString() {
		return getUIEvent().toString();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.ISemanticEvent#getUIEvent()
	 */
	public com.windowtester.recorder.event.ISemanticEvent getUIEvent() {
		return _event;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == ISemanticEvent.class)
			return getUIEvent();
		return null;
	}
	
	
}
