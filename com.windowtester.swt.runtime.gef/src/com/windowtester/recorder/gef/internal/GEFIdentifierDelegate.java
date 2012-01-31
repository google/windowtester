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
package com.windowtester.recorder.gef.internal;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.gef.internal.finder.FigureIdentifierService;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.locator.PaletteButtonLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.identifier.IWidgetIdentifierDelegate;

/**
 * Contributes identifiers to the SWT runtime.  
 * <p>
 * Note that this delegate itself may be overriden if a figure identifier advisor
 * is applicable to the given event to identify.
 * 
 */
public class GEFIdentifierDelegate implements IWidgetIdentifierDelegate {

	private final IFigureIdentifier paletteButtonIdentifier = (IFigureIdentifier) new PaletteButtonLocator().getAdapter(IFigureIdentifier.class);
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.identifier.IWidgetIdentifierDelegate#identify(java.lang.Object)
	 */
	public ILocator identify(Object toIdentify) {
		if (!(toIdentify instanceof Event))
			return null;
		
		Event event = (Event)toIdentify;
		IFigure figure = getFigure(event);
		
		if (notOnACanvas(event, figure))
			return checkForPaletteButton(event, figure);
		
		ILocator overridingIdentifier = getOverridingIdentifier(figure);
		if (overridingIdentifier != null)
			return overridingIdentifier;
		
		//note: figure may be null
		ILocator locator = FigureIdentifierService.getDefault().identify(figure, event);
		return locator;
		
	}


	private ILocator checkForPaletteButton(Event event, IFigure figure) {
		if (paletteButtonIdentifier == null)
			return null;
		return paletteButtonIdentifier.identify(figure, event);
	}

	private boolean notOnACanvas(Event event, IFigure figure) {
		return figure == null && !(event.widget instanceof FigureCanvas);
	}

	private IFigure getFigure(Event event) {
		Widget widget = event.widget;
		if (!(widget instanceof FigureCanvas))
			return null;
		
		FigureCanvas canvas = (FigureCanvas)widget;
		IFigure contents = canvas.getContents();
		if (contents == null)
			return null;
		
		return contents.findFigureAt(event.x, event.y);
	}

	private ILocator getOverridingIdentifier(IFigure figure) {
		return FigureIdentifierAdvisorManager.identify(figure);
	}

}
