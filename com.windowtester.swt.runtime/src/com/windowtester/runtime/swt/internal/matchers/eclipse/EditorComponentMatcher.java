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
package com.windowtester.runtime.swt.internal.matchers.eclipse;


import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.EditorPart;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;

/**
 * Matcher that matches widgets that are components of an {@link EditorPart}.
 */
public class EditorComponentMatcher extends PartComponentMatcher {

	/** The name of the target editor part */
	private final String partName;
		
	/**
	 * Create a matcher for components of the given editor (identified by part name).
	 * @param partName  the name of the editor part
	 */
	public EditorComponentMatcher(String partName) {
		this.partName = partName;
	}
		
	@Override
	protected Control getPartControl() throws WidgetSearchException {
		return EditorFinder.getEditorControl(partName);
	}
	
	/**
	 * Return a String representation of this view component matcher.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
        return "Editor Component matcher (" + partName + ")";
    }
	
	
}
