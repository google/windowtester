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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.Workbench;

import com.windowtester.runtime.swt.internal.finder.eclipse.WorkbenchFinder;
import com.windowtester.runtime.swt.locator.eclipse.PerspectiveLocator;

/**
 * Command to show a perspective.
 */
@SuppressWarnings("restriction")
public class ShowPerspectiveCommand extends WorkbenchCommand {

	private final PerspectiveLocator perspective;
	
	public static ShowPerspectiveCommand forPerspective(PerspectiveLocator perspective) {
		return new ShowPerspectiveCommand(perspective);
	}

	public ShowPerspectiveCommand(PerspectiveLocator perspective) {
		this.perspective = perspective;
	}

	public void run(final IWorkbenchWindow window) throws CommandException {
		ensurePerspectiveExists();
		syncExec(new CommandRunnable() {
			public void run() throws ExecutionException {
				openPerspective(perspective.getDescriptor());
			}
		});
	}
	
	
	private void ensurePerspectiveExists() throws CommandException {
		IPerspectiveDescriptor desc = perspective.getDescriptor();
		if (desc == null)
			throw new CommandException("Perspective (" + perspective + ") not found in registry");
	}
	
	

	private void openPerspective(IPerspectiveDescriptor perspective) throws ExecutionException {
		openPerspective(perspective, WorkbenchFinder.getActiveWindow());
	}
	
	private final void openPerspective(IPerspectiveDescriptor perspective,
			final IWorkbenchWindow activeWorkbenchWindow)
			throws ExecutionException {

		final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		String perspectiveId = perspective.getId();

		try {
			//trick copied from org.eclipse.ui.handlers.ShowPerspectiveHandler
			if (activePage == null) {
				final IWorkbench workbench = PlatformUI.getWorkbench();
				IAdaptable input = ((Workbench) workbench).getDefaultPageInput();
				// perspective opening in new windows changed it from 
				// IWorkbenchWindow.openPage to IWorkbench.showPerspective
				workbench.showPerspective(perspectiveId, activeWorkbenchWindow, input);
			} else {
				final IWorkbench workbench = PlatformUI.getWorkbench();
				workbench.showPerspective(perspectiveId, activeWorkbenchWindow);
			}
		} catch (WorkbenchException e) {
			throw new ExecutionException(e.getMessage());
		}
	}
	
	
}
