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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;
import com.windowtester.runtime.gef.internal.finder.GEFFinder;
import com.windowtester.runtime.gef.internal.finder.MultiplePartsFoundException;
import com.windowtester.runtime.gef.internal.finder.PartException;
import com.windowtester.runtime.gef.internal.finder.PartNotFoundException;
import com.windowtester.runtime.gef.internal.matchers.PaletteItemPartMatcher;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;

/**
 * A Palette Item {@link IEditPartFinder}.
 */
public class PalettePartFinder implements IEditPartFinder, Serializable {

	
	private static final long serialVersionUID = -4100340068777034108L;

	private transient final IGEFEditPartMatcher partMatcher;
	private final IEditorLocator editor;
	private final String itemPath;
		
	public PalettePartFinder(String itemPath, IEditorLocator locator) {
		this.partMatcher = new PaletteItemPartMatcher(itemPath);
		this.itemPath    = itemPath;
		this.editor      = locator;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////////

	
	public IGEFEditPartMatcher getPartMatcher() {
		return partMatcher;
	}

	public IEditorLocator getEditor() {
		return editor;
	}

	public String getItemPath() {
		return itemPath;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// 
	//
	////////////////////////////////////////////////////////////////////////////////

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.locator.IEditPartFinder#getEditPart()
	 */
	public EditPart getEditPart() throws MultiplePartsFoundException, PartNotFoundException {
		
		/*
		 * TODO: this should be parceled into a search scope object
		 * and unified with IFigureSearchScope
		 */
		ScrollingGraphicalViewer paletteViewer = getPaletteViewer();
		if (paletteViewer == null)
			return null;
		
		EditPart[] parts = GEFFinder.getDefault().findAllEditParts(paletteViewer, getPartMatcher());
		
		parts = pruneBoundlessParts(parts);
		assertExactlyOne(parts);
		return parts[0];
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

	private GraphicalViewer getViewer() {
		IEditorLocator editor = getEditor();
		String viewerLabel = editor == null ? ".*" : editor.getPartName();
		return GEFFinder.getDefault().findViewer(viewerLabel);
	}
	
		
	////////////////////////////////////////////////////////////////////////////////
	//
	// Utilities
	//
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test if this figure has bounds.
	 * <p>
	 * The need for this emerged in GEF3.4 where there are multiple Marquee palette tool entries (but only one with bounds).
	 */
	private boolean boundless(IFigure figure) {
		Rectangle bounds = figure.getBounds();
		if (bounds == null)
			return true;
		return bounds.x == 0 && bounds.y == 0 && bounds.height == 0 && bounds.width ==0;
	}
	private boolean boundless(EditPart editPart) {
		if (!(editPart instanceof GraphicalEditPart))
			return false;
		return boundless(((GraphicalEditPart)editPart).getFigure());
	}
	
	private EditPart[] pruneBoundlessParts(EditPart[] parts) {
		List pruned = new ArrayList();
		for (int i = 0; i < parts.length; i++) {
			if (!boundless(parts[i]))
				pruned.add(parts[i]);
		}
		return (EditPart[]) pruned.toArray(new EditPart[]{});
	}
	
	private void assertExactlyOne(EditPart[] parts)
			throws PartNotFoundException, MultiplePartsFoundException {
		if (parts.length == 0)
			PartException.notFound(getPartMatcher(), getEditor());
		if (parts.length > 1)
			PartException.multiple(getPartMatcher(), getEditor());
	}
	
}
