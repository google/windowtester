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

import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.util.ICommand;

public class EventSequenceCommandFactory implements IEventSequenceCommandFactory {

	
	private static final ICommand[] EMPTY_LIST = new ICommand[0];
	
	private final IEventSequence _sequence;

	public class RemoveCommand implements ICommand {
		
		private final ISemanticEvent _event;
		private int _index;

		RemoveCommand(ISemanticEvent event) {
			_index = indexOf(event, getSequence());
			_event = event;
		}
		
		public void exec() {
			getSequence().remove(_event);
		}

		public void undo() {
			getSequence().add(_index, _event);
		}
	}
	
	public class AddCommand implements ICommand {
		
		private final ISemanticEvent _event;

		AddCommand(ISemanticEvent event) {
			_event = event;
		}
		
		public void exec() {
			getSequence().add(_event);
		}

		public void undo() {
			getSequence().remove(_event);
		}
	}
	
	
	public class RemoveEveryCommand implements ICommand {
		
		private ISemanticEvent[] _events;
		
		public void exec() {
			_events = getSequence().getEvents();
			getSequence().removeAll();
		}

		public void undo() {
			for (int i = 0; i < _events.length; i++) {
				getSequence().add(_events[i]);
			}
		}
	}

	
	public class RemoveAllCommand implements ICommand {
		
		private ISemanticEvent[] _events;
		private int[] _indices;
		
		
		RemoveAllCommand(ISemanticEvent[] events) {
			_events = events;
			_indices = new int[events.length];
		}
		
		public void exec() {
			for (int i = 0; i < _events.length; i++) {
				_indices[i] = indexOf(_events[i], _events);
			}
			getSequence().removeAll(_events);
		}

		public void undo() {
			for (int i = 0; i < _events.length; i++) {
				getSequence().add(_indices[i], _events[i]);
			}
		}
	}

	public class NotApplicableCommand implements ICommand {
		public void exec() {
			// TODO Auto-generated method stub
		}
		public void undo() {
			// TODO Auto-generated method stub	
		}
	}
	
	
	
	public EventSequenceCommandFactory(IEventSequence sequence) {
		_sequence = sequence;
	}
	
	public final IEventSequence getSequence() {
		return _sequence;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceCommandFactory#add(com.windowtester.ui.core.model.ISemanticEvent)
	 */
	public ICommand add(ISemanticEvent event) {
		return new AddCommand(event);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceCommandFactory#removeEvery()
	 */
	public ICommand removeEvery() {
		return new RemoveEveryCommand();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceCommandFactory#removeAll(com.windowtester.ui.core.model.ISemanticEvent[])
	 */
	public ICommand removeAll(ISemanticEvent[] events) {
		return new RemoveAllCommand(events);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceCommandFactory#remove(com.windowtester.ui.core.model.ISemanticEvent)
	 */
	public ICommand remove(ISemanticEvent event) {
		return new RemoveCommand(event);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.ui.util.ICommandProvider#getCommands(java.lang.Object[])
	 */
	public ICommand[] getCommands(Object[] elems) {
		if (elems.length == 0)
			return empty();
		if (elems.length == 1) {
			return singleCommand((ISemanticEvent) elems[0]);
		}
		return multiCommand(array(elems));
	}



	private ICommand[] multiCommand(Object[] elems) {
		// TODO Auto-generated method stub
		return null;
	}

	private ICommand[] singleCommand(ISemanticEvent semanticEvent) {
		return array(remove(semanticEvent));
	}

	protected ICommand notAppicable() {
		return new NotApplicableCommand();
	}

	
	/////////////////////////////////////////////////////////////////////////////////////
	//
	// Utility
	//
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static ICommand[] empty() {
		return EMPTY_LIST;
	}
	
	private static ICommand[] array(ICommand cmd) {
		return new ICommand[]{cmd};
	}
	
	private static ISemanticEvent[] array(Object[] elems) {
		ISemanticEvent[] events = new ISemanticEvent[elems.length];
		for (int i = 0; i < elems.length; i++) {
			events[i] = (ISemanticEvent)elems[i];
		}
		return events;
	}
	
	
	private static int indexOf(ISemanticEvent event, IEventSequence sequence) {
		return indexOf(event, sequence.getEvents());
	}
	
	private static final int indexOf(Object o, Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == o)
				return i;
		}
		return -1;
	}

	
}
