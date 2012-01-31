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
package com.windowtester.runtime.gef.internal;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;

import com.windowtester.runtime.gef.internal.finder.IGEFPartMapper;
import com.windowtester.runtime.gef.internal.helpers.PaletteAccessor;


public interface IGEFFinder extends IGEFPartMapper {

	EditPart[] findAllEditParts();

	EditPart[] findAllEditParts(IGEFEditPartMatcher matcher);
	
	EditPart[] findAllEditParts(GraphicalViewer viewer, IGEFEditPartMatcher matcher);
	
	IGEFEditPartReference[] findAllEditPartReferences(GraphicalViewer viewer, IGEFEditPartMatcher matcher);

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IGEFPartMapper#findEditPart(org.eclipse.draw2d.IFigure)
	 */
	EditPart findEditPart(IFigure figure);

	IFigure getFigure(EditPart part);

	GraphicalViewer findViewer(String partNameOrPattern);

	EditPart[] findAllEditParts(IFigure figure);
	
	PaletteAccessor findPaletteForActiveEditor();
	
	GraphicalViewer findViewerForActiveEditor();
	

}
