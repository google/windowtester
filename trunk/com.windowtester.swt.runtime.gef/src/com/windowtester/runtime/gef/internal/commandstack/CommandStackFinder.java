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
package com.windowtester.runtime.gef.internal.commandstack;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;

import com.windowtester.runtime.gef.internal.finder.GEFFinder;

/**
 * Finder for active command stacks.
 *
 */
public class CommandStackFinder {

	public static CommandStack findStackForActiveEditor() {
		GraphicalViewer viewer = GEFFinder.getDefault().findViewerForActiveEditor();
		if (viewer == null)
			return null;
		EditDomain editDomain = viewer.getEditDomain();
		if (editDomain == null)
			return null;
		return editDomain.getCommandStack();
	}
	
	
}
