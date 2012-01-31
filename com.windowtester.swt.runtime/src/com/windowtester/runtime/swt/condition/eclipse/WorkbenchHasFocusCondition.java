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

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IUIConditionHandler;
import com.windowtester.runtime.condition.UICondition;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;

/**
 * Tests to see if the workbench shell has focus.   
 * 
 */
public class WorkbenchHasFocusCondition extends UICondition implements IUIConditionHandler {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		return ShellFinder.getActiveShell(getDisplay()) != null;
	}

	/**
	 * Give the workbench focus (if it does not already).
	 * 
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 */
	public void handle(IUIContext ui) throws Exception {
		ShellFinder.bringRootToFront(getDisplay());
	}

	private Display getDisplay() {
		return Display.getDefault();
	}


}
