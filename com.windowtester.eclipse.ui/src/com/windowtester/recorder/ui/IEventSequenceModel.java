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

import org.eclipse.jface.action.IAction;

import com.windowtester.ui.core.model.IEvent;
import com.windowtester.ui.core.model.IEventGroup;
import com.windowtester.ui.core.model.IEventSequence;
import com.windowtester.ui.core.model.ISemanticEvent;

public interface IEventSequenceModel extends IEventSequence {

	
	public static interface ISequenceListener {
		void sequenceChanged();
	}
	
	void addListener(ISequenceListener listener);
	void removeListener(ISequenceListener listener);
	
	
	/**
	 * Undo the last action.
	 * <p>
	 * This will pop the last command off the command stack and undo it. Undo
	 * can be called to undo commands until there are none left to process 
	 * ({@link #canUndo()} returns <code>true</code>).
	 *  
	 */
	void undo();
	
	/**
	 * Are there commands that can be undone?
	 */
	boolean canUndo();
	
	/**
	 * Is the sequence empty?
	 */
	boolean isEmpty();
	
	/**
	 * Select the given events.
	 * <p>
	 * NOTE: passing <code>null</code> deselects.
	 */
	void select(ISemanticEvent[] events);
	
	/**
	 * Get the selected events.
	 */
	ISemanticEvent[] getSelection();
	
	/**
	 * Are any elements selected?
	 */
	boolean hasSelection();
	
	/**
	 * Get actions appropriate for the current selection.
	 */
	IAction[] getActions();
	
	/**
	 * Act on a delete click.
	 */
	void clickDelete();
	
	/**
	 * Session start.
	 */
	void sessionStarted();

	/**
	 * Session end.
	 */
	void sessionEnded();
	
	/**
	 * @param selected
	 * @return
	 * @since 3.9.1
	 */
	IEventGroup group(IEvent[] selected);
	
	
}
