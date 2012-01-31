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

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.views.IViewDescriptor;

import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * Command to zoom a part.
 *
 */
public class ZoomPartCommand extends WorkbenchCommand {

	private final ViewLocator view;
	
	public static ZoomPartCommand forView(ViewLocator view) {
		return new ZoomPartCommand(view);
	}

	public ZoomPartCommand(ViewLocator view) {
		this.view = view;
	}

	public void run(final IWorkbenchWindow window) throws CommandException {
		ensureViewExists();
		ensureViewShowing(window);
		syncExec(new CommandRunnable() {
			public void run() throws Exception {
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						IViewDescriptor descriptor = view.getDescriptor();
						IWorkbenchPartReference partRef = ViewFinder.getViewRef(descriptor);
						if (partRef != null) {
							page.toggleZoom(partRef);
							
						}
					}
				}
			}
		});
	}
	
	
	private void ensureViewShowing(IWorkbenchWindow window) throws CommandException {
		new ShowViewCommand(view).run(window);
	}

	private void ensureViewExists() throws CommandException {
		IViewDescriptor viewDesc = view.getDescriptor();
		if (viewDesc == null)
			throw new CommandException("View (" + view + "} not found in registry");
	}
	
	
	
}
