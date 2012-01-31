package com.windowtester.test.recorder.ui;

import com.windowtester.ui.util.CommandStack;
import com.windowtester.ui.util.ICommand;
import com.windowtester.ui.util.ICommandStack;

import junit.framework.TestCase;

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
public class CommandStackTest extends TestCase {

	
	class CommandStub implements ICommand {

		int execCount;
		int undoCount;
		
		public void exec() {
			++execCount;
		}
		public void undo() {
			++undoCount;
		}
	}
	
	
	public void testExecAndUndo() {
		ICommandStack stack = new CommandStack();
		CommandStub cmd = new CommandStub();
		stack.exec(cmd);
		assertEquals(1, cmd.execCount);
		assertEquals(0, cmd.undoCount);
		stack.undo();
		assertEquals(1, cmd.execCount);
		assertEquals(1, cmd.undoCount);
	}
	
	
	
	public void testMutlipleUndos() {
		ICommandStack stack = new CommandStack();
		CommandStub cmd1 = new CommandStub();
		CommandStub cmd2 = new CommandStub();
		CommandStub cmd3 = new CommandStub();
		stack.exec(cmd1);
		stack.exec(cmd2);
		stack.exec(cmd3);
		assertEquals(1, cmd1.execCount);
		assertEquals(1, cmd2.execCount);
		assertEquals(1, cmd3.execCount);
		stack.undo();
		assertEquals(0, cmd1.undoCount);
		assertEquals(0, cmd2.undoCount);
		assertEquals(1, cmd3.undoCount);
		stack.undo();
		assertEquals(0, cmd1.undoCount);
		assertEquals(1, cmd2.undoCount);
		assertEquals(1, cmd3.undoCount);
		stack.undo();
		assertEquals(1, cmd1.undoCount);
		assertEquals(1, cmd2.undoCount);
		assertEquals(1, cmd3.undoCount);
	}
	
	
}
