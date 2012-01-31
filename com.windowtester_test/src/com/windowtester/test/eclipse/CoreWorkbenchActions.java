package com.windowtester.test.eclipse;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.ShowViewAction;

import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.finder.eclipse.WorkbenchFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewExplorer;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

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
@SuppressWarnings("restriction")
public class CoreWorkbenchActions {

	static ViewExplorer viewExplorer = new ViewExplorer();
	
	public static void showViewNamed(final String viewName, final IWorkbenchWindow window) {
		ensureViewExists(viewName);
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				//pick your poison: backdoor access or copy paste...
				new ShowViewAction(window, viewExplorer.findView(viewName), false) {}.run();
			}
		});
		//TODO: consider some kind of validation/synchronization code here...
	}
	
	
	//TODO: move to common (along with some kind of condition story?)
	private static void ensureViewExists(String viewName) {
		if (viewExplorer.findView(viewName) == null)
			throw new RuntimeException("View (" +viewName + "} not found in registry");
	}



	public static void showViewNamed(String viewName) {
		showViewNamed(viewName, WorkbenchFinder.getActiveWindow());
	}
	
}
