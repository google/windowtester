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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;

import com.windowtester.eclipse.ui.usage.ProfiledAction;

/**
 * An action builder.
 */
public class ActionProvider {

	public class EventSequenceAction extends ProfiledAction {

		private final ICommand command;

		public EventSequenceAction(String text, ImageDescriptor image, ICommand command) {
			super(text, image);
			this.command = command;
			setId(command.getClass().getName());
		}
		
		@Override
		public void doRun() {
			getCommandStack().exec(command);
		}
		
		
	}
	
	private final ICommandProvider _commandProvider;
	private final ICommandLabelProvider _labelProvider;
	private final ICommandStack _commandStack;

	public ActionProvider(ICommandProvider cp, ICommandLabelProvider lp, ICommandStack cs) {
		_commandProvider = cp;
		_labelProvider   = lp;
		_commandStack    = cs;
	}
	
	public final ICommandProvider getCommandProvider() {
		return _commandProvider;
	}
	
	public final ICommandLabelProvider getLabelProvider() {
		return _labelProvider;
	}
	
	public final ICommandStack getCommandStack() {
		return _commandStack;
	}
	
	public final IAction[] getActions(Object[] elems) {
		final ICommand[] commands = getCommandProvider().getCommands(elems);
		IAction[] actions = new IAction[commands.length];
		for (int i = 0; i < commands.length; i++) {
			actions[i] = createAction(commands[i]);
		}
		return actions;
	}

	private IAction createAction(final ICommand command) {
		String text = getLabelProvider().getText(command);
		ImageDescriptor image = getLabelProvider().getImage(command);
		return new EventSequenceAction(text, image, command);
	}
	
}
