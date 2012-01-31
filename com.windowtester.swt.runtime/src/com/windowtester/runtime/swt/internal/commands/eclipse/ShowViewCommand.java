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
package com.windowtester.runtime.swt.internal.commands.eclipse;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.ShowViewAction;
import org.eclipse.ui.views.IViewDescriptor;

import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * Command to show a view.
 */
@SuppressWarnings("restriction")
public class ShowViewCommand extends WorkbenchCommand {

	private final ViewLocator view;
	
	public static ShowViewCommand forView(ViewLocator view) {
		return new ShowViewCommand(view);
	}

	public ShowViewCommand(ViewLocator view) {
		this.view = view;
	}

	public void run(final IWorkbenchWindow window) throws CommandException {
		ensureViewExists();
		syncExec(new CommandRunnable() {
			public void run() {
				//pick your poison: back door access or copy paste...
				new ShowViewAction(window, view.getDescriptor(), false) {}.run();
			}
		});
	}
	
	
	private void ensureViewExists() throws CommandException {
		IViewDescriptor viewDesc = view.getDescriptor();
		if (viewDesc == null)
			throw new CommandException("View (" + view + "} not found in registry");
	}
	
	
	
}
