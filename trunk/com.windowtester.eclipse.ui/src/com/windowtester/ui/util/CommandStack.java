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
package com.windowtester.ui.util;

import java.util.Stack;

/**
 * Command stack implementation.
 */
public class CommandStack implements ICommandStack {
	
	private final Stack<ICommand> commands;
	
	public CommandStack() {
		this(new Stack<ICommand>());
	}
	
	//constructor injection for testing purposes
	public CommandStack(Stack<ICommand> backingStack) {
		commands = backingStack;
	}
	
	
	protected final Stack<ICommand> getCommands() {
		return commands;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.ui.util.ICommandStack#exec(com.windowtester.ui.util.ICommand)
	 */
	public void exec(ICommand command) {
		command.exec();
		push(command);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.ui.util.ICommandStack#undo()
	 */
	public void undo() {
		ICommand popped = pop();
		popped.undo();
	}
	
	/**
	 * @exception  EmptyStackException  if this stack is empty.
	 */
	protected final ICommand peek() {
		return getCommands().peek();
	}
	
	/**
	 * @exception  EmptyStackException  if this stack is empty.
	 */
	protected final ICommand pop() {
		return getCommands().pop();
	}
	
	protected final void push(ICommand command) {
		getCommands().push(command);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.ui.util.ICommandStack#isEmpty()
	 */
	public boolean isEmpty() {
		return getCommands().isEmpty();
	}

}
