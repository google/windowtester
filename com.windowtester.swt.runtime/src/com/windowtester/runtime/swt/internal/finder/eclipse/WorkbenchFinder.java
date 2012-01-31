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
package com.windowtester.runtime.swt.internal.finder.eclipse;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Workbench access helpers.
 */
public class WorkbenchFinder {

	public static IWorkbenchPage getActivePage() {
		IWorkbench workbench = getWorkbench();
		if (workbench == null)
			return null;
		IWorkbenchWindow window = getActiveWindow(workbench);
		if (window == null)
			return null;
		return window.getActivePage();
	}

	public static IWorkbenchWindow getActiveWindow(final IWorkbench workbench) {
		final IWorkbenchWindow[] window = new IWorkbenchWindow[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				window[0] = workbench.getActiveWorkbenchWindow();
			}
		});
		return window[0];
	}

	public static IWorkbench getWorkbench() {
		final IWorkbench[] workbench = new IWorkbench[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				workbench[0] = PlatformUI.getWorkbench();
			}
		});
	
		TestCase.assertNotNull(workbench[0]);
		return workbench[0];
	}


	public static IWorkbenchWindow getActiveWindow() {
		return getActiveWindow(getWorkbench());
	}

}
