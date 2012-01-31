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
import java.util.Iterator;
import java.util.List;

import com.windowtester.recorder.ui.EventSequenceOptimizer;
import com.windowtester.ui.core.model.IEvent;
import com.windowtester.ui.core.model.IEventGroup;
import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.core.model.ISemanticEvent;

public class EventSequence implements IEventSequence {


	private static final String DEFAULT_GROUP_LABEL = "group";
	private ArrayList events; //concrete class because we depend on clear() being supported

	public List getEvents0() {
		if (events == null)
			events = new ArrayList();
		return events;
	}

	public IEventSequence add(ISemanticEvent event) {
		getEvents0().add(event);
		postChange();
		return this;
	}

	public IEventSequence addAll(ISemanticEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			getEvents0().add(events[i]);
		}
		postChange();
		return this;
	}
	
	public IEventSequence add(int index, ISemanticEvent event) {
		getEvents0().add(index, event);
		postChange();
		return this;
	}
	
	
	public ISemanticEvent[] getEvents() {
		return (ISemanticEvent[]) getEvents0().toArray(new ISemanticEvent[]{});
	}

	public IEventGroup group(IEvent[] events) {
		int insertIndex = -1;
		List eventList = getEvents0();
		for (int i = 0; i < events.length; i++) {
			IEvent event = events[i];
			if (insertIndex == -1)
				if (eventList.contains(event))
					insertIndex = eventList.indexOf(event);
			eventList.remove(event);
		}
		EventGroup group = new EventGroup(DEFAULT_GROUP_LABEL);
		group.addAll(events);
		add(insertIndex, group);
		postChange();
		return group;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#removeAll()
	 */
	public IEventSequence removeAll() {
		getEvents0().clear();
		postChange();
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#removeAll(com.windowtester.ui.core.model.ISemanticEvent[])
	 */
	public IEventSequence removeAll(ISemanticEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			remove(events[i]);
		}
		postChange();
		return this;
	}


	private void postChange() {
		doPostChange();
	}

	private void doPostChange() {
		EventSequenceOptimizer.optimize(this);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#remove(com.windowtester.ui.core.model.ISemanticEvent)
	 */
	public IEventSequence remove(ISemanticEvent event) {
		if (!getEvents0().remove(event))
			deepRemove(event);
		postChange();
		return this;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#isEmpty()
	 */
	public boolean isEmpty() {
		return events.isEmpty();
	}

	//tunnel into groups and text entry events
	public void deepRemove(ISemanticEvent event) {
		for (Iterator iter = getEvents0().iterator(); iter.hasNext(); ) {
			Object next = iter.next();
			if (next instanceof IEventGroup) 
				((IEventGroup)next).remove((IEvent) event); //groups can only contain events...
		}
	}
	
}
