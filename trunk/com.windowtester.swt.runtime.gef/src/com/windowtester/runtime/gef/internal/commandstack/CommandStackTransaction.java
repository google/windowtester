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
package com.windowtester.runtime.gef.internal.commandstack;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CommandStackEventListener;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;

/**
 * Tracks events pushed on and popped off the command stack.
 */
public class CommandStackTransaction {
	
	/**
	 * Timeout (in milliseconds) for waits on transaction completion.
	 * <p>
	 * Note: this is only public for testing purposes.
	 */
	public static final long TRANSACTION_TIMEOUT = 5000; //TODO: should this be longer?
	

	/**
	 * Listener on the command stack.  Since this object will be accessed from
	 * two threads, it is appropriately synchronized.  Accesses are as follows:
	 * <ul>
	 * <li> App thread: stack change notifications (modifying command list).</li>
	 * <li> Test thread: command list querying (for size).</li>
	 * </ul>
	 *
	 *
	 */
	private class StackListener implements CommandStackEventListener {
		
		//this could probably be a stack...
		List<Object> commands = new ArrayList<Object>();
		
		public synchronized void stackChanged(CommandStackEvent event) {
			if (CommandStackEventType.forEvent(event).isPre())
				addEvent(event);
			else {
				//TODO: consider testing/asserting that the remove succeeded (but there may be some leftover from before we start)
				removeEvent(event);
			}
		}

		private boolean removeEvent(CommandStackEvent event) {
			//System.out.println("removing command: " + event);
			return commands.remove(event.getSource());
		}

		private boolean addEvent(CommandStackEvent event) {
			//System.out.println("adding command: " + event);
			return commands.add(event.getSource());
		}
		
		public synchronized boolean containsUnfinishedCommmands() {
			return commands.size() > 0;
		}
	}

	/**
	 * A special transaction for the non-existent edit domain case.
	 * 
	 * NOTE: public for testing.
	 * 
	 */
	public final static CommandStackTransaction UNCHECKED_TRANSACTION = new CommandStackTransaction() {
		public boolean isComplete() {
			return true;
		}
		public CommandStackTransaction start() {
			//no-op
			return this;
		}
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.gef.internal.commandstack.CommandStackTransaction#stop()
		 */
		public void stop() {
			//no-op
		}
		public Object runInUI(UIRunnable runnable, IUIContext arg1)
				throws WidgetSearchException {
			//just run without waiting for transaction completion
			return runnable.runWithResult();
		}
	};
	
	
	private final StackListener stackListener = new StackListener();	
	private CommandStack stack;
	private boolean started;


	/**
	 * Create a transaction for the stack associated with the current active editor.
	 * Note: the stack may be null.
	 */
	public static CommandStackTransaction forActiveEditor() {
		return new CommandStackTransaction().forStack(CommandStackFinder.findStackForActiveEditor());
	}
	
	/**
	 * Create a transaction for the given stack.  Note: the stack may be null.
	 */
	public CommandStackTransaction forStack(CommandStack stack) {
		if (stack == null)
			return UNCHECKED_TRANSACTION;
		this.stack = stack;
		return this;
	}

	public CommandStackTransaction start() {
		started = true;
		stack.addCommandStackEventListener(stackListener);
		return this;
	}
	
	public void stop() {
		stack.removeCommandStackEventListener(stackListener);
	}
	
	public boolean isComplete() {
		boolean result = !stackListener.containsUnfinishedCommmands();
//		if (!result)
//			System.out.println("commandstack is not empty");
		return result;
	}

	public boolean isStarted() {
		return started;
	}
	
	/**
	 * Run this runnable as a transaction.
	 */
	public Object runInUI(UIRunnable runnable, IUIContext ui) throws WidgetSearchException {
		start();
		try {
			Object result = runnable.runWithResult();
			//TODO: do we need an extra waitForIdle here to make sure the commands get on the stack?
			ui.wait(TransactionCompleteCondition.forTransaction(this), TRANSACTION_TIMEOUT);
			return result;
		} finally {
			stop();
		}
	}

}
