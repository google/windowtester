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
package com.windowtester.recorder.ui;

import com.windowtester.recorder.ui.events.EventParser;
import com.windowtester.recorder.ui.events.ParsedEvent;
import com.windowtester.recorder.ui.events.ParsedEventSequence;
import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.core.model.ISemanticEvent;

/**
 * Helper for optimizing event sequences.
 */
public class EventSequenceOptimizer {


	private static IEventSequence current;

	public static void optimize(IEventSequence sequence) {
		
		if (isEmpty(sequence))
			return;
		
		if (alreadyOptimizing())
			return;
		
		cacheCurrentTarget(sequence);
		doOptmize(sequence);
		cacheCurrentTarget(null);
	}


	private static void doOptmize(IEventSequence sequence) {
		
		ParsedEventSequence parsedEvents = new ParsedEventSequence();
		
		ISemanticEvent[] events = sequence.getEvents();
		for (int i = 0; i < events.length; i++) {
			ISemanticEvent event = events[i];
			ParsedEvent parsedEvent = EventParser.parse(event);
			parsedEvents.add(parsedEvent);
		}
		sequence.removeAll();
		sequence.addAll(parsedEvents.toPresentable().getEvents());
	}


	private static boolean isEmpty(IEventSequence sequence) {
		return sequence.getEvents().length == 0;
	}

	private static void cacheCurrentTarget(IEventSequence sequence) {
		EventSequenceOptimizer.current = sequence;
	}

	private static boolean alreadyOptimizing() {
		return current != null;
	}

}
