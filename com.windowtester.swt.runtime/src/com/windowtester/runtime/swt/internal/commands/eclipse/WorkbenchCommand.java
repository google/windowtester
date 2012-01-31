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

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;

import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.finder.eclipse.WorkbenchFinder;

/**
 * Base class for workbench commands.
 */
public abstract class WorkbenchCommand {

	
	/**
	 * NOTE: it is callers responsibility to validate post exec.
	 * @throws WidgetNotFoundException
	 */
	public final void run() throws CommandException {
		run(WorkbenchFinder.getActiveWindow());
	}


	protected abstract void run(IWorkbenchWindow activeWindow) throws CommandException;
	
	
	public void syncExec(final CommandRunnable runnable) throws CommandException {
		final Exception[] ex = new Exception[1];
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				try {
					runnable.run();
				} catch(Exception e) {
					ex[0] = e;
				}
			}
		});
		if (ex[0] != null)
			throw new CommandException(ex[0].getMessage());
	}
	
}
