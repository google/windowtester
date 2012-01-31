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
package com.windowtester.runtime.gef.internal.identifier;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

import com.windowtester.internal.runtime.matcher.ByNameClassMatcher;
import com.windowtester.runtime.gef.internal.finder.GEFFinder;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.internal.matchers.ParentWidgetMatcher;
import com.windowtester.runtime.swt.internal.matcher.ContainedInWidgetMatcher;

public abstract class AbstractFigureIdentifier implements IFigureIdentifier {

	
	//TODO: this can be improved...
	protected boolean isInPalette(IFigure figure) {
		if (figure == null)
			return false;
		return getPaletteParentControlMatcher().matches(figure);
	}

	
	protected EditPart findPart(IFigure figure) {
		if (figure == null)
			return null;
		EditPart[] parts = GEFFinder.getDefault().findAllEditParts(figure);
		if (parts.length != 1)
			return null;
		
		EditPart part = parts[0];
		return part;
	}

	private ParentWidgetMatcher getPaletteParentControlMatcher() {
		return new ParentWidgetMatcher(new ContainedInWidgetMatcher(new ByNameClassMatcher("org.eclipse.gef.ui.palette.FlyoutPaletteComposite$PaletteComposite")));
	}
}
