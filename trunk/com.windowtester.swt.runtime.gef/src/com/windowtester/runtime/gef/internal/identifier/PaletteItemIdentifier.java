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
import org.eclipse.swt.widgets.Event;

import com.windowtester.runtime.gef.internal.matchers.PaletteItemPartMatcher;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;

/**
 * An identifier for palette items.
 */
public class PaletteItemIdentifier extends AbstractFigureIdentifier {
	
	/* (non-Javadoc)
	 * 
	 * @see com.windowtester.runtime.gef.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure)
	 */
	public ILocator identify(IFigure figure, Event event) {
		
		if (figure == null)
			return null;
		
		//test containment
		if (!isInPalette(figure))
			return null;

		EditPart part = findPart(figure);
		if (part == null)
			return null;
				
		//note that scoping happens elsewhere
		return new PaletteItemLocator(getPath(part));
	}

	private String getPath(EditPart part) {
		return PaletteItemPartMatcher.getPath(part);
	}
}