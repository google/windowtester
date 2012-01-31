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
package com.windowtester.test.recorder.ui;

import static com.windowtester.test.codegen.CodeGenFixture.fakeKeyEntry;
import static com.windowtester.test.codegen.CodeGenFixture.fakeSelectEvent;
import static com.windowtester.test.recorder.ui.FakeEvents.buttonSelect1;
import static com.windowtester.test.recorder.ui.FakeEvents.buttonSelect2;
import static com.windowtester.test.recorder.ui.FakeEvents.buttonSelect3;
import static com.windowtester.test.recorder.ui.FakeEvents.keyA;
import static com.windowtester.test.recorder.ui.FakeEvents.keyB;
import static com.windowtester.test.recorder.ui.FakeEvents.keyCR;
import static com.windowtester.test.recorder.ui.FakeEvents.keyTAB;
import junit.framework.TestCase;

import org.eclipse.swt.widgets.Button;

import com.windowtester.recorder.ui.EventSequenceOptimizer;
import com.windowtester.recorder.ui.events.EventParser;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.internal.corel.model.Event;
import com.windowtester.ui.internal.corel.model.EventSequence;


/**
 * @author Phil Quitslund
 * 
 * @see {@link ParsedEventSequenceTest}
 */
public class EventSequenceOptimizerTest extends TestCase {

	private IEventSequence sequence = new EventSequence();;
	

	
	public void testA() throws Exception {
		addEvents(fakeKeyEntry('f'), fakeKeyEntry('o'), fakeKeyEntry('o'), fakeSelectEvent(Button.class, new ButtonLocator("OK")));
		assertEquals(2, sequence.getEvents().length);
	}
	
	public void testB() throws Exception {
		addEvents(buttonSelect1, fakeKeyEntry('f'), fakeKeyEntry('o'), fakeKeyEntry('o'), buttonSelect2);
		assertEquals(3, sequence.getEvents().length);
		assertEquals(buttonSelect1, sequence.getEvents()[0]);
		assertEquals(buttonSelect2, sequence.getEvents()[2]);
	}
	
	public void testC() throws Exception {	
		addAndOptimize(sequence, fakeKeyEntry('h'));
		addAndOptimize(sequence, fakeKeyEntry('o'));
		addAndOptimize(sequence, buttonSelect1);
		addAndOptimize(sequence, fakeKeyEntry('h'));
		addAndOptimize(sequence, fakeKeyEntry('o'));
		addAndOptimize(sequence, buttonSelect2);
		addAndOptimize(sequence, fakeKeyEntry('h'));
		addAndOptimize(sequence, fakeKeyEntry('o'));
		addAndOptimize(sequence, buttonSelect3);
		assertEquals(6, sequence.getEvents().length);
		assertEquals(buttonSelect1, sequence.getEvents()[1]);
		assertEquals(buttonSelect2, sequence.getEvents()[3]);
		assertEquals(buttonSelect3, sequence.getEvents()[5]);
	}
	

	public void testTabs() throws Exception {
		addEvents(keyA, keyTAB, keyB);
		assertEquals(keyA, sequence.getEvents()[0]);
		assertEquals(keyTAB, sequence.getEvents()[1]);
		assertEquals(keyB, sequence.getEvents()[2]);
	}
	


//	public void testBackspaceClobbers() throws Exception {
//		addEvents(keyA, keyBackSpace, keyB, buttonSelect1);
//		assertEquals(keyB, sequence.getEvents()[0]);
//		assertEquals(buttonSelect1, sequence.getEvents()[1]);
//	}
//	
//	public void testBackspaceClobbers2() throws Exception {
//		addEvents(keyA, keyB, keyBackSpace, keyBackSpace, keyC, buttonSelect1);
//		assertEquals(keyC, sequence.getEvents()[0]);
//		assertEquals(buttonSelect1, sequence.getEvents()[1]);
//	}
	
	
	public void testTerminators() throws Exception {
		assertTerminates(keyTAB, keyCR);
	}
	
	private void assertTerminates(Event ...events) {
		for (Event event : events) {
			assertTrue(EventParser.isTextEventTerminator(event));
		}
	}

	private void addEvents(Event ... events) {
		for (Event event : events) {
			sequence.add(event);
			EventSequenceOptimizer.optimize(sequence);
		}
	}

	private IEventSequence addAndOptimize(IEventSequence sequence, Event event) {
		sequence.add(event);
		EventSequenceOptimizer.optimize(sequence);
		return sequence;
	}
	
}
