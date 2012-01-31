package com.windowtester.test.recorder.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;

import junit.framework.TestCase;

import com.windowtester.recorder.ui.EventSequenceModel;
import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.recorder.ui.SequenceCommandLabelProvider;
import com.windowtester.recorder.ui.IEventSequenceModel.ISequenceListener;
import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.util.ICommand;

import static com.windowtester.recorder.ui.IActionConstants.*;

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
public class EventSequenceModelTest extends TestCase {

	
	//TODO: add event sequence model to presenter and wire up
	
	public void testInitialState() {
		IEventSequenceModel model = new EventSequenceModel();
		assertEquals(0, size(model));
	}
	
	public void testAdd() {
		IEventSequenceModel model = new EventSequenceModel();
		ISemanticEvent event = stubEvent();
		model.add(event);
		assertEquals(1, size(model));
		assertEquals(event, model.getEvents()[0]);
	}
	
	public void XtestAddDoesNotSelect() {
		IEventSequenceModel model = new EventSequenceModel();
		ISemanticEvent event = stubEvent();
		model.add(event);
		assertEquals(0, model.getSelection().length);
	}
	
	
	public void testAddFiresChange() {
		IEventSequenceModel model = new EventSequenceModel();
		final boolean[] changed = new boolean[1];
		model.addListener(new ISequenceListener() {
			public void sequenceChanged() {
				changed[0] = true;
			}
		});
		ISemanticEvent event = stubEvent();
		model.add(event);
		assertTrue(changed[0]);
	}
	
	public void testCanUndo() {
		IEventSequenceModel model = new EventSequenceModel();
		ISemanticEvent event = stubEvent();
		model.add(event);
		assertTrue(model.canUndo());
	}
	
	
	public void testUndo() {
		IEventSequenceModel model = new EventSequenceModel();
		ISemanticEvent event = stubEvent();
		model.add(event);
		assertTrue(model.canUndo());
		model.undo();
		assertEquals(0, size(model));
	}

	
	public void testSingleSelect() {
		IEventSequenceModel model = new EventSequenceModel();
		ISemanticEvent event = stubEvent();
		assertFalse(model.hasSelection());
		model.add(event);
		model.select(null);
		assertFalse(model.hasSelection());
		model.select(array(event));
		assertTrue(model.hasSelection());
		ISemanticEvent[] selected = model.getSelection();
		assertEquals(1, selected.length);
	}

	public void testMultiSelect() {
		IEventSequenceModel model = new EventSequenceModel();
		ISemanticEvent event = stubEvent();
		ISemanticEvent event2 = stubEvent();
		model.add(event);
		model.add(event2);
		model.select(array(event, event2));
		ISemanticEvent[] selected = model.getSelection();
		assertEquals(2, selected.length);
	}
	
	
	public void testGetActionsFromEmptySeq() {
		IEventSequenceModel model = new EventSequenceModel();		
		IAction[] actions = model.getActions();
		assertEquals(0, actions.length);
	}
	

	public void testDeselect() {
		IEventSequenceModel model = stubEventModel();
		model.add(stubEvent());
		model.select(null);
		assertFalse(model.hasSelection());
	}
	
	public void testGetActionsFromSeqWithoutSelection() {
		IEventSequenceModel model = stubEventModel();
		model.add(stubEvent());
		model.select(null);
		IAction[] actions = model.getActions();
		assertEquals(0, actions.length);
	}

	public void testGetActionsFromSeqWithSelection() {
		//stub model that does not try and load images
		IEventSequenceModel model = stubEventModel();
		ISemanticEvent e = stubEvent();
		model.add(e);
		model.select(array(e));
		IAction[] actions = model.getActions();
		assertEquals(1, actions.length);
		assertEquals(DELETE_ACTION_TEXT, actions[0].getText());
		
	}

	public void testSessionStartClearsSequence() {
		IEventSequenceModel model = stubEventModel();
		ISemanticEvent e = stubEvent();
		model.add(e);
		model.sessionStarted();
		assertEquals(0, model.getEvents().length);
	}
	
	
	
	//stub model that does not try and load images
	private EventSequenceModel stubEventModel() {
		return new EventSequenceModel() {
			@Override
			protected SequenceCommandLabelProvider createSequenceLabelProvider() {
				return new SequenceCommandLabelProvider() {
					@Override
					public ImageDescriptor getImage(ICommand command) {
						return null;
					}
				};
			}
		};
	}
	
	
	
	
	
//	public void testUndoneAddRestoresProperSelection_UNIMPL() {
//		fail("unimplemented");
//	}
//	
//	
//	public void testRemoveAll_UNIMPL() {
//		fail("unimpl");
//		IEventSequenceModel model = new EventSequenceModel();
//		assertEquals(0, size(model));
//	}
	
	
	private ISemanticEvent[] array(ISemanticEvent ... event) {
		return event;
	}
	
	
	private int size(IEventSequenceModel model) {
		return model.getEvents().length;
	}
	
	

	
	
	private ISemanticEvent stubEvent() {
		return new ISemanticEvent() {};
	}

	
	
	
}
