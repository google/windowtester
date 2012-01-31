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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.widgets.Event;

import com.windowtester.internal.debug.LogHandler;
import com.windowtester.runtime.gef.internal.finder.AnchorFinder;
import com.windowtester.runtime.gef.internal.finder.GEFFinder;
import com.windowtester.runtime.gef.internal.finder.IAnchorInfo;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.internal.helpers.PaletteAccessor.ToolDescriptor;
import com.windowtester.runtime.gef.internal.locator.provisional.api.AnchorLocator;
import com.windowtester.runtime.gef.locator.IFigureLocator;
import com.windowtester.runtime.locator.ILocator;

/**
 * An identifier for anchor positions on a figure.
 */
public class AnchorIdentifier extends AbstractFigureIdentifier {

	private IFigureIdentifier delegate;
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure, org.eclipse.swt.widgets.Event)
	 */
	public ILocator identify(IFigure figure, Event event) {
		if (!isConnectionToolActive())
			return null;
		IAnchorInfo anchor = findAnchor(figure, event);
		if (anchor == null)
			return null;
		IFigureLocator hostFigure = getHostFigure(figure, event);	
		if (hostFigure == null)
			return null;
		
		return new AnchorLocator(anchor.getPosition(), hostFigure);
	}

	private boolean isConnectionToolActive() {
		ToolDescriptor tool = GEFFinder.getDefault().findPaletteForActiveEditor().getActiveTool();
		if (tool == null)
			return false;
		return tool.isConnection();
	}

	private IFigureLocator getHostFigure(IFigure figure, Event event) {
		ILocator hostFigure = delegate.identify(figure, event);
		if (!(hostFigure instanceof IFigureLocator)) {
			LogHandler.log(new IllegalStateException("host locator unexpectedly of type: " + hostFigure.getClass()));
			return null;
		}
		return (IFigureLocator)hostFigure;
	}

	private IAnchorInfo findAnchor(IFigure figure, Event event) {
		if (event == null)
			return null;
		return AnchorFinder.forFigureAtPoint(figure, new Point(event.x, event.y));
	}

	public IFigureIdentifier withDelegate(IFigureIdentifier delegate) {
		this.delegate = delegate;
		return this;
	}

}
