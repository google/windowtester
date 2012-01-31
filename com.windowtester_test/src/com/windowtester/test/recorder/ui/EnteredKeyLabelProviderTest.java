package com.windowtester.test.recorder.ui;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticTextEntryEvent;
import com.windowtester.ui.internal.corel.model.EnteredKeyLabelProvider;
import com.windowtester.ui.internal.corel.model.Event;

import junit.framework.TestCase;

import static com.windowtester.test.recorder.ui.FakeEvents.*;

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
public class EnteredKeyLabelProviderTest extends TestCase {

	 
	public void testBackspaceHandling() throws Exception {
		SemanticTextEntryEvent text = text(keyA, keyB, keyBackSpace);
		String label = EnteredKeyLabelProvider.getLabel(text);
		assertEquals("'a'", label);
	}

	
	public void testBackspaceHandling2() throws Exception {
		SemanticTextEntryEvent text = text(keyA, keyB, keyBackSpace, keyA);
		String label = EnteredKeyLabelProvider.getLabel(text);
		assertEquals("'aa'", label);
	}

	
	private SemanticTextEntryEvent text(Event ...events) {
		List<SemanticKeyDownEvent> keys = new ArrayList<SemanticKeyDownEvent>();
		for (Event event : events) {
			keys.add((SemanticKeyDownEvent) event.getUIEvent());
		}
		return new SemanticTextEntryEvent(keys.toArray(new SemanticKeyDownEvent[]{}));
	}
}
