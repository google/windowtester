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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.windowtester.runtime.condition.ICondition;

/**
 * Tests for an open {@link EditorPart}.
 * 
 */
public class OpenEditorCondition implements ICondition {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		return !getEditors().isEmpty();	
	}

	// TODO[pq]: move to utility class
    private List<IEditorReference> getEditors() {
    	final List<IEditorReference> editors = new ArrayList<IEditorReference>();
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    if (window != null) {
                        IWorkbenchPage page = window.getActivePage();
                        if (page != null) {
                            editors.addAll(Arrays.asList(page.getEditorReferences()));
                        }
                    }
                }
            });
        return editors;
    }
	
	
	
	
}
