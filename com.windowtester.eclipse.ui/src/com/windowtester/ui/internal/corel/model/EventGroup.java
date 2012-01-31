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

import java.util.ArrayList;
import java.util.List;

import com.windowtester.ui.core.model.IEvent;
import com.windowtester.ui.core.model.IEventGroup;

public class EventGroup implements IEventGroup {

	private List _events;

	private String _name;

	public EventGroup(String name) {
		setName(name);
	}

	private List getEvents0() {
		if (_events == null)
			_events = new ArrayList();
		return _events;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IEventGroup#add(com.windowtester.util.core.model.IEvent)
	 */
	public void add(IEvent event) {
		getEvents0().add(event);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IEventGroup#getEvents()
	 */
	public IEvent[] getEvents() {
		return (IEvent[]) getEvents0().toArray(new IEvent[]{});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IEventGroup#getName()
	 */
	public String getName() {
		return _name;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IEventGroup#addAll(com.windowtester.util.core.model.IEvent[])
	 */
	public void addAll(IEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			getEvents0().add(events[i]);
		}
	}

	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IEventGroup#remove(com.windowtester.util.core.model.IEvent)
	 */
	public void remove(IEvent event) {
		getEvents0().remove(event);
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.util.core.model.IEventGroup#setName(java.lang.String)
	 */
	public void setName(String name) {
		_name = name;
	}


}
