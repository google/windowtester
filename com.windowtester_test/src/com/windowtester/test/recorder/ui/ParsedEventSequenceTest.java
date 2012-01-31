package com.windowtester.test.recorder.ui;

import static com.windowtester.test.recorder.ui.FakeEvents.buttonSelect1;
import static com.windowtester.test.recorder.ui.FakeEvents.keyA;
import static com.windowtester.test.recorder.ui.FakeEvents.keyB;
import static com.windowtester.test.recorder.ui.FakeEvents.keyC;
import static com.windowtester.test.recorder.ui.FakeEvents.keyTAB;
import junit.framework.TestCase;

import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.ui.events.EventParser;
import com.windowtester.recorder.ui.events.ParsedEvent;
import com.windowtester.recorder.ui.events.ParsedEventSequence;
import com.windowtester.recorder.ui.events.ParsedKeyEvent;
import com.windowtester.recorder.ui.events.ParsedTextEvent;
import com.windowtester.recorder.ui.events.ParsedWidgetEvent;
import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.internal.corel.model.Event;

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
public class ParsedEventSequenceTest extends TestCase {

	ParsedEventSequence sequence = new ParsedEventSequence();
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		System.out.println(sequence.toString());
	}
	
	public void testJustStandardKeys() throws Exception {
		add(keyA, keyB);
		assertLength(1);
		ParsedTextEvent text = get(0);
		assertEq(keyA,  text.keys()[0]);
		assertEq(keyB,  text.keys()[1]);
	}
	
	public void testJustStandardKeys2() throws Exception {
		add(keyA, keyB, keyC);
		assertLength(1);
		ParsedTextEvent text = get(0);
		assertEq(keyA,  text.keys()[0]);
		assertEq(keyB,  text.keys()[1]);
		assertEq(keyC,  text.keys()[2]);
	}
	
	
	public void testKeysAndButton() throws Exception {
		add(keyA, keyB, keyC, buttonSelect1, keyA);
		assertLength(3);
		ParsedTextEvent text = get(0);
		assertEq(keyA,  text.keys()[0]);
		assertEq(keyB,  text.keys()[1]);
		assertEq(keyC,  text.keys()[2]);
		ParsedWidgetEvent button = get(1);
		assertEq(buttonSelect1, button);
		ParsedKeyEvent key = get(2);
		assertEq(keyA, key);
	}
	

	public void testKeysAndTab() throws Exception {
		add(keyA, keyB, keyTAB, keyA);
		assertLength(3);
		ParsedTextEvent text = get(0);
		assertEq(keyA,  text.keys()[0]);
		assertEq(keyB,  text.keys()[1]);
		ParsedKeyEvent tab = get(1);
		assertEq(keyTAB, tab);
		ParsedKeyEvent key = get(2);
		assertEq(keyA, key.key());
	}
	
	
	private void assertEq(Event event, ParsedWidgetEvent widgetEvent) {
		assertEq(event, widgetEvent.event());
	}



	private void assertEq(Event event, ParsedKeyEvent keyEvent) {
		assertEq(event, keyEvent.key());
	}

	private void assertEq(Event event, IUISemanticEvent semanticEvent) {
		assertEquals(event.getUIEvent(), semanticEvent);
	}

	private void assertEq(Event event, ISemanticEvent semanticEvent) {
		assertEquals(event, semanticEvent);
	}
	

	@SuppressWarnings("unchecked")
	private <T extends ParsedEvent > T get(int index) {
		return (T) sequence.get(index);
	}


	private void assertLength(int expectedLength) {
		assertEquals(expectedLength, sequence.size());
	}

	private void add(Event ... events) {
		for (Event event : events) {
			sequence.add(EventParser.parse(event));
		}
	}
	
	
	
}
