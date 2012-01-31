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

import java.util.ArrayList;
import java.util.List;

import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticTextEntryEvent;
import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.internal.corel.model.Event;


public class ParsedTextEvent extends AbstractParsedTextEvent {

	List keys = new ArrayList();
	
	public ParsedTextEvent(SemanticTextEntryEvent text) {
		SemanticKeyDownEvent[] keys2 = text.getKeys();
		for (int i = 0; i < keys2.length; i++) {
			add(new ParsedKeyEvent(keys2[i], null));
		}
		
	}

	public ParsedTextEvent() { }

	public void add(ParsedKeyEvent parsedKeyEvent) {
		keys.add(parsedKeyEvent);
	}

	public ParsedKeyEvent[] parsedKeys() {
		return (ParsedKeyEvent[]) keys.toArray(new ParsedKeyEvent[]{});
	}
	
	public SemanticKeyDownEvent[] keys() {
		ParsedKeyEvent[] parsedKeys = parsedKeys();
		SemanticKeyDownEvent[] keys = new SemanticKeyDownEvent[parsedKeys.length];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = parsedKeys[i].key();
		}
		return keys;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.ParsedEvent#consume(com.windowtester.recorder.ui.ParsedEvent)
	 */
	public ParsedEvent consume(ParsedEvent event) {
		add((ParsedKeyEvent)event);
		return this;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.events.ParsedEvent#addTo(com.windowtester.ui.core.model.IEventSequence)
	 */
	public void addTo(IEventSequence sequence) {
		sequence.add(new Event(new SemanticTextEntryEvent(keys())));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return keys.toString();
	}
	

}
