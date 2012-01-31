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

import java.io.Serializable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.ui.IEditorPart;

import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;

/**
 * A helper for querying a GEF editor.
 */
public class EditorAccessor implements Serializable {

	private static final long serialVersionUID = 1L;

	private final IEditorLocator editor;

	public EditorAccessor(IEditorLocator editor) {
		this.editor = editor;
	}

	public IEditorLocator getEditor() {
		return editor;
	}
	
	public IEditorPart getEditorPart() {
		return EditorFinder.getEditorPart(getEditor().getPartName());
	}

	public GraphicalViewer getViewer() {
		IEditorPart editorPart = getEditorPart();
		if (editorPart == null)
			return null;
		return adaptToGraphicalViewer(editorPart);
	}

	public static GraphicalViewer adaptToGraphicalViewer(final IEditorPart editorPart) {
		return (GraphicalViewer) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				return (GraphicalViewer)editorPart.getAdapter(GraphicalViewer.class);
			}
		});
	}
	
	
	public IFigure getContentPane() {
		GraphicalViewer viewer = getViewer();
		if (viewer == null)
			return null;
		
		EditPart contents = viewer.getContents();
		if (!(contents instanceof GraphicalEditPart))
			return null;
		
		return ((GraphicalEditPart)contents).getContentPane();
	}

	public PaletteViewer getPaletteViewer() {
		GraphicalViewer viewer = getViewer();
		if (viewer == null)
			return null;
		EditDomain editDomain = viewer.getEditDomain();
		if (editDomain == null)
			return null;
		return editDomain.getPaletteViewer();
	}
	
	
}
