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

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import com.windowtester.runtime.swt.locator.eclipse.PerspectiveLocator;

/**
 * Command to close a perspective.
 */
public class ClosePerspectiveCommand extends WorkbenchCommand {

	final PerspectiveLocator perspective;
	
	public static ClosePerspectiveCommand forPerspective(PerspectiveLocator perspective) {
		return new ClosePerspectiveCommand(perspective);
	}

	public ClosePerspectiveCommand(PerspectiveLocator perspective) {
		this.perspective = perspective;
	}

	public void run(final IWorkbenchWindow window) throws CommandException {
		ensurePerspectiveExists();
		syncExec(new CommandRunnable() {
			public void run() {
				closePerspective(perspective.getDescriptor(), window);
			}
		});
	}
	
	
	protected void ensurePerspectiveExists() throws CommandException {
		IPerspectiveDescriptor desc = perspective.getDescriptor();
		if (desc == null)
			throw new CommandException("Perspective (" + perspective + "} not found in registry");
	}
	
	protected void closePerspective(IPerspectiveDescriptor perspective, IWorkbenchWindow window) {
		if (perspective == null)
			return;
		if (window == null)
			return;	
		IWorkbenchPage page = window.getActivePage();
		page.closePerspective(perspective, true, true);
	}
	
	
	
}
