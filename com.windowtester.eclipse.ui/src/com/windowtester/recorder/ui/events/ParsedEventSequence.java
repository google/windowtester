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
package com.windowtester.recorder.ui.events;

import java.util.Iterator;
import java.util.Stack;

import com.windowtester.recorder.ui.events.ParsedEvent;
import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.internal.corel.model.EventSequence;

public class ParsedEventSequence {

	private final Stack events = new Stack();
	
	public void add(ParsedEvent event) {
		if (!events.isEmpty()) {
			ParsedEvent top = (ParsedEvent) events.peek();
			if (top.consumes(event)) {
				events.pop();
				top = top.consume(event); // notice identity may have changed...
				events.push(top);
				return;
			}
		}
		events.push(event);
	}

	public int size() {
		return events.size();
	}

	public ParsedEvent get(int index) {
		return (ParsedEvent) events.get(index);
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ParsedSequence [" + events.toString() + "]";
	}
	
	public IEventSequence toPresentable() {
		IEventSequence sequence = new EventSequence();
		for (Iterator iterator = events.iterator(); iterator.hasNext();) {
			ParsedEvent event = (ParsedEvent) iterator.next();
			event.addTo(sequence);
		}
		return sequence;
		
	}
	
}
