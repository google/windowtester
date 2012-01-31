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
package com.windowtester.runtime.swt.condition.eclipse;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IUIConditionHandler;
import com.windowtester.runtime.condition.UICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

/**
 * Tests to see if the workbench shell is maximized.   
 * 
 */
public class WorkbenchIsMaximizedCondition extends UICondition implements IUIConditionHandler {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		final Shell workbenchRoot = ShellFinder.getWorkbenchRoot();
		return DisplayReference.getDefault().execute(new Callable<Boolean>(){
			public Boolean call() throws Exception {
				return workbenchRoot.getMaximized();
			}
		});
	}

	/**
	 * Maximize the workbench.
	 * 
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 */
	public void handle(IUIContext ui) throws Exception {
		final Shell workbenchRoot = ShellFinder.getWorkbenchRoot();
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				workbenchRoot.setMaximized(true);
			}
		});
	}

}
