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
package com.windowtester.runtime.swt.locator.eclipse;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;

/**
 * Locates the active editor.  
 * <p>
 * NOTE: the active editor is (re)evaluated on each call to test.
 */
public class ActiveEditorLocator extends EditorLocator {

	private static final long serialVersionUID = -4613964161899776952L;

	private static final String NULL_EDITOR = null; //this is used as a sentinel value to indicate that the editor find failed

	/**
	 * @see EditorLocator#EditorLocator(String, int)
	 */
	public ActiveEditorLocator(int variation) {
		super(NULL_EDITOR, variation); //NOTE: this value will be (re)set on each test
	}

	/**
	 * @see EditorLocator#EditorLocator(String)
	 */
	public ActiveEditorLocator() {
		this(WT.NONE);
	}
		
	private static String doFindActiveEditorPartName() {
		if (!Platform.isRunning()) //NOTE: this is an optimization for speeding up testing
			return null;
		IEditorPart activeEditor = EditorFinder.getActiveEditor();
		if (activeEditor == null) {
			return NULL_EDITOR;
		}
		return activeEditor.getTitle();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		if (getPartName() == NULL_EDITOR)
			return new IWidgetLocator[]{};
		return super.findAll(ui);
	}

	/**
	 * Calculate the name of the currently active editor part.
	 * 
	 * @see com.windowtester.runtime.swt.locator.eclipse.EditorLocator#getPartName()
	 */
	public String getPartName() {
		return doFindActiveEditorPartName();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ActiveEditorLocator";
	}
}
