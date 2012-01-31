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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;

import com.windowtester.ui.core.model.IEvent;
import com.windowtester.ui.core.model.IEventGroup;
import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.internal.corel.model.EventSequence;
import com.windowtester.ui.util.ActionProvider;
import com.windowtester.ui.util.CommandStack;
import com.windowtester.ui.util.ICommand;
import com.windowtester.ui.util.ICommandStack;

/**
 * A wrapper for a sequence that adds change event notification.
 */
public class EventSequenceModel implements IEventSequenceModel {

	
	private static final ISemanticEvent[] EMPTY_SELECTION = new ISemanticEvent[]{};
			
	private final IEventSequence _sequence = new EventSequence();
	private final List _listeners = new ArrayList();
	
	//initialized lazily since we need a reference to 'this'
	private IEventSequenceCommandFactory _commandFactory;
	private ActionProvider _actionProvider;
	
	private ISemanticEvent[] _selection = EMPTY_SELECTION;
	

	protected final IEventSequence getSequence() {
		return _sequence;
	}
	
	private List getListeners() {
		return _listeners;
	}
	
	protected final ActionProvider getActionProvider() {
		if (_actionProvider == null)
			_actionProvider = createActionProvider();
		return _actionProvider;
	}

	/**
	 * Override to inject.
	 */
	protected ActionProvider createActionProvider() {
		return new ActionProvider(getCommandFactory(), createSequenceLabelProvider(), new CommandStack());
	}

	/**
	 * Override to inject.
	 */
	protected SequenceCommandLabelProvider createSequenceLabelProvider() {
		return new SequenceCommandLabelProvider();
	}
	
	protected final IEventSequenceCommandFactory getCommandFactory() {
		if (_commandFactory == null)
			_commandFactory = createCommandFactory();
		return _commandFactory;
	}

	/**
	 * Override to inject.
	 */
	protected EventSequenceCommandFactory createCommandFactory() {
		return new EventSequenceCommandFactory(getSequence());
	}
	
	private ICommandStack getCommandStack() {
		return getActionProvider().getCommandStack();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#getEvents()
	 */
	public ISemanticEvent[] getEvents() {
		return getSequence().getEvents();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#removeAll()
	 */
	public IEventSequence removeAll() {
		exec(getCommandFactory().removeEvery());
		return this;
	}

	private void exec(ICommand cmd) {
		getCommandStack().exec(cmd);
		changed();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#removeAll(com.windowtester.ui.core.model.ISemanticEvent[])
	 */
	public IEventSequence removeAll(ISemanticEvent[] events) {
		exec(getCommandFactory().removeAll(events));
		return this;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#remove(com.windowtester.ui.core.model.ISemanticEvent)
	 */
	public IEventSequence remove(ISemanticEvent event) {
		exec(getCommandFactory().remove(event));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#add(com.windowtester.ui.core.model.ISemanticEvent)
	 */
	public IEventSequence add(ISemanticEvent event) {
		exec(getCommandFactory().add(event));
		select(new ISemanticEvent[]{event});
		
		return this;
		
	}
	
	//note: this is not undoable...
	public IEventSequence addAll(ISemanticEvent[] events) {
		_sequence.addAll(events);
		select(events);
		return this;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.ui.core.model.IEventSequence#add(int, com.windowtester.ui.core.model.ISemanticEvent)
	 */
	public IEventSequence add(int index, ISemanticEvent event) {
		//... TODO!
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#addListener(com.windowtester.recorder.ui.IEventSequenceModel.ISequenceListener)
	 */
	public void addListener(ISequenceListener listener) {
		getListeners().add(listener);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#removeListener(com.windowtester.recorder.ui.IEventSequenceModel.ISequenceListener)
	 */
	public void removeListener(ISequenceListener listener) {
		getListeners().remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#undo()
	 */
	public void undo() {
		getCommandStack().undo();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#canUndo()
	 */
	public boolean canUndo() {
		return !getCommandStack().isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#isEmpty()
	 */
	public boolean isEmpty() {
		return getEvents().length == 0;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#sessionStarted()
	 */
	public void sessionStarted() {
		//NOTICE: this is NOT undoable
		getSequence().removeAll();
		changed();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#sessionEnded()
	 */
	public void sessionEnded() {
		// no-op
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#select(com.windowtester.ui.core.model.ISemanticEvent[])
	 */
	public void select(ISemanticEvent[] events) {
		setSelection(events);
		changed();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#getSelection()
	 */
	public ISemanticEvent[] getSelection() {
		return _selection;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#hasSelection()
	 */
	public boolean hasSelection() {
		return getSelection().length > 0;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#getActions()
	 */
	public IAction[] getActions() {
		return getActionProvider().getActions(getSelection());
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#clickDelete()
	 */
	public void clickDelete() {
		removeAll(getSelection());
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.IEventSequenceModel#group(com.windowtester.ui.core.model.ISemanticEvent[])
	 */
	public IEventGroup group(IEvent[] events) {
		return _sequence.group(events);
	}
	
	private void setSelection(ISemanticEvent[] selection) {
		_selection = (selection == null) ? EMPTY_SELECTION : selection;
	}
	
	private void changed() {
		for (Iterator iterator = getListeners().iterator(); iterator.hasNext();) {
			ISequenceListener listener = (ISequenceListener) iterator.next();
			listener.sequenceChanged();
		}
	}
	
}
