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
package com.windowtester.runtime.swt.internal.condition.eclipse;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.windowtester.runtime.condition.ICondition;

/**
 * A condition to test if there are any open "dirty" editors.
 */
public class DirtyEditorCondition implements ICondition {


	//TODO: promote this (or a functional equivalent) to API.
	
	/**
	 * Determine if any open editors are "dirty".
	 * WARNING! This method MUST be called on the UI thread,
	 * 
	 * @return <code>true</code> if at least one editor has unsaved changes, else
	 *         <code>false</code>
	 */
	protected static boolean anyUnsavedChanges0() {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchPage[] pages = windows[i].getPages();
			for (int j = 0; j < pages.length; j++) {
				IEditorReference[] editorRefs = pages[j].getEditorReferences();
				for (int k = 0; k < editorRefs.length; k++) {
					IEditorReference each = editorRefs[k];
					if (each.isDirty())
						return true;
				}
			}
		}
		return false;
	}

	
	/**
	 * Determine if any open editors are "dirty".
	 * 
	 * @return <code>true</code> if at least one editor has unsaved changes, else
	 *         <code>false</code>
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		final boolean result[] = new boolean[] { false };
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				result[0] = anyUnsavedChanges0();
			}
		});
		return result[0];
	}
}
