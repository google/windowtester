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

import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.core.model.ISemanticEvent;

public class ParsedKeyEvent extends AbstractParsedTextEvent {

	private final SemanticKeyDownEvent key;
	private final ISemanticEvent event;

	
	public ParsedKeyEvent(SemanticKeyDownEvent key, ISemanticEvent event) {
		this.key = key;
		this.event = event;
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.AbstractParsedTextEvent#consumes(com.windowtester.recorder.ui.ParsedEvent)
	 */
	public boolean consumes(ParsedEvent event) {
		if (isTextEventTerminator(key))
			return false;
		return super.consumes(event);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.ParsedEvent#consume(com.windowtester.recorder.ui.ParsedEvent)
	 */
	public ParsedEvent consume(ParsedEvent event) {
		ParsedTextEvent text = new ParsedTextEvent();
		text.add(this);
		text.add((ParsedKeyEvent)event);
		return text;
	}

	public SemanticKeyDownEvent key() {
		return key;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.events.ParsedEvent#addTo(com.windowtester.ui.core.model.IEventSequence)
	 */
	public void addTo(IEventSequence sequence) {
		sequence.add(event);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return key.toString();
	}

}
