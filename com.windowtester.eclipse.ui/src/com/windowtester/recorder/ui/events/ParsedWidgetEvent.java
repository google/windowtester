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

import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.core.model.ISemanticEvent;

public class ParsedWidgetEvent extends ParsedEvent {

	
	private final ISemanticEvent event;

	ParsedWidgetEvent(ISemanticEvent event) {
		this.event = event;
	}
	
	public ISemanticEvent event() {
		return event;
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return event.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.events.ParsedEvent#addTo(com.windowtester.ui.core.model.IEventSequence)
	 */
	public void addTo(IEventSequence sequence) {
		sequence.add(event);
	}
}
