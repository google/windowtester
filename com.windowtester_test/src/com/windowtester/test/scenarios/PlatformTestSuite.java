package com.windowtester.test.scenarios;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.test.util.Logger;

import junit.framework.TestResult;
import junit.framework.TestSuite;

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
public class PlatformTestSuite extends TestSuite {

	public PlatformTestSuite(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestSuite#run(junit.framework.TestResult)
	 */
	@Override
	public void run(TestResult result) {
		ensureWorkbenchInFront();
		super.run(result);
	}

	private void ensureWorkbenchInFront() {
		Logger.log("bringing workbench to front");
		ShellFinder.bringRootToFront(Display.getDefault());
	}
	
	@SuppressWarnings("unused")
	private void maximizeWorkbench() {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				window.getShell().setMaximized(true);
			}
		});
	}
	
	
}
