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

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Event;

import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.locator.ILocator;

public class FigureCanvasIdentifier extends AbstractFigureIdentifier {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure, org.eclipse.swt.widgets.Event)
	 */
	public ILocator identify(IFigure figure, Event event) {
		if (isInPalette(figure))
			return null;
		Object widget = event.widget;
		if (!(widget instanceof FigureCanvas))
			return null;
		
		/*
		 * Note: this is SIMPLE to start (e.g., no scoping) --- this will have to be fixed!
		 */
		return new FigureCanvasXYLocator(event.x, event.y);
	}

}
