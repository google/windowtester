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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.swt.internal.condition.eclipse.DirtyEditorCondition;

/**
 * Tests whether (and ensures that) all dirty editors are saved.
 * 
 */
public class AllEditorsSavedCondition implements IConditionHandler {

	private final DirtyEditorCondition dirtyEditors = new DirtyEditorCondition();
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.UICondition#test()
	 */
	public boolean test() {
		return !dirtyEditors.test();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 */
	public void handle(IUIContext ui) throws Exception {
        //setExpectedDelay(ui, 180000);
        //ui.handleConditions();
        saveAllEditors();
        //ui.handleConditions();
	}

	private void saveAllEditors() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				IWorkbench workbench = PlatformUI.getWorkbench();
				workbench.saveAllEditors(false);
			}
		});
	}

	

	
}
