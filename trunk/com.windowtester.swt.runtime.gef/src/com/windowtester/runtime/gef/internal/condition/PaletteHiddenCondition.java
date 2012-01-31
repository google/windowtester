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
package com.windowtester.runtime.gef.internal.condition;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.gef.internal.selectors.IPaletteViewerProvider;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder.EditorNotFoundException;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder.MultipleEditorsFoundException;

/**
 * Tests to see if a palette is hidden by checking the visibility of its
 * Control as provided by {@link PaletteViewer#getControl()}.
 * 
 * @see PaletteShowingCondition
 *
 */
public class PaletteHiddenCondition implements ICondition {

	
	private class PaletteViewerProvider implements IPaletteViewerProvider {

		public PaletteViewer getViewer() {
			IEditorReference editorRef = getEditorRef();
			if (editorRef == null)
				return null;
			return getPaletteViewer(editorRef);
		}
		
		private PaletteViewer getPaletteViewer(IEditorReference editorRef) {
			IEditorPart editor = editorRef.getEditor(true);
			GraphicalViewer graphicalViewer = (GraphicalViewer) editor.getAdapter(GraphicalViewer.class);
			EditDomain editDomain = graphicalViewer.getEditDomain();
			PaletteViewer paletteViewer = editDomain.getPaletteViewer();
			return paletteViewer;
		}
	}
	
	
	private IEditorReference editorRef;

	public PaletteHiddenCondition(FigureCanvas canvas) throws MultipleEditorsFoundException {
		try {
			editorRef = EditorFinder.findContainingEditor(canvas);
		} catch (EditorNotFoundException e) {
			//this is not an error -- the editor will just be null in this case
		} 
	}

	protected IEditorReference getEditorRef() {
		return editorRef;
	}
	
	/** 
	 * NOTE: will return false if there is no editor associated with this canvas.
	 * 
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		return !(new PaletteShowingCondition(new PaletteViewerProvider()).test());
	}



}
