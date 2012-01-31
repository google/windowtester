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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IConditionHandler;

/**
 * Tests whether (and ensures that) editors are closed.  If any editors are
 * dirty, their contents are first saved.
 */
public class AllEditorsClosedCondition implements IConditionHandler {

	
	private final OpenEditorCondition openEditor = new OpenEditorCondition();
	
	/**
	 * Tests to see if all editors are closed.
	 * 
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		return !openEditor.test();
	}
	
	/**
	 * Closes all editors.
	 * 
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 */
	public void handle(IUIContext ui) throws Exception {
		ui.ensureThat(new AllEditorsSavedCondition());
        closeEditors();
		
	}

	private void closeEditors() {
		Display.getDefault().syncExec(new Runnable() {
            public void run() {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (window != null) {
                    IWorkbenchPage page = window.getActivePage();
                    if (page != null) {
                        page.closeAllEditors(false);
                    }
                }
            }
        });
	}
	
	

}
