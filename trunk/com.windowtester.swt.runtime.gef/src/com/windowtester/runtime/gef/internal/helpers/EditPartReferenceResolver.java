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
package com.windowtester.runtime.gef.internal.helpers;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.IEditorPart;

import com.windowtester.runtime.gef.internal.GEFEditPartReference;
import com.windowtester.runtime.gef.internal.IGEFEditPartReference;
import com.windowtester.runtime.gef.internal.IGEFPartLocator;
import com.windowtester.runtime.gef.internal.finder.GEFFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;

public class EditPartReferenceResolver {

	public IGEFEditPartReference[] findAll(IGEFPartLocator locator) {
		
		IEditorPart editorPart = getEditorPart(locator);	
		if (editorPart == null)
			return emptyRef();
		
		GraphicalViewer viewer = (GraphicalViewer)editorPart.getAdapter(GraphicalViewer.class);
		if (viewer == null)
			return emptyRef();
		
		EditPart editPart = viewer.getContents();
		if (editPart == null)
			return emptyRef();
		
		return GEFFinder.getDefault().findAllEditPartReferences(viewer, locator.getPartMatcher());		
	}

	private IGEFEditPartReference[] emptyRef() {
		return GEFEditPartReference.emptyList();
	}

	private IEditorPart getEditorPart(IGEFPartLocator locator) {
		EditorLocator editorLocator = locator.getEditorLocator();
		return EditorFinder.getEditorPart(editorLocator.getPartName());
	}

}
