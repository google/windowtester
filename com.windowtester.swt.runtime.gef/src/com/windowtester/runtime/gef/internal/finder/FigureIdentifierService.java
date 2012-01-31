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
package com.windowtester.runtime.gef.internal.finder;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.gef.internal.finder.scope.UnscopedSearch;
import com.windowtester.runtime.gef.internal.identifier.AnchorIdentifier;
import com.windowtester.runtime.gef.internal.identifier.NamedEditPartFigureIdentifier;
import com.windowtester.runtime.gef.internal.identifier.NamedFigureIdentifier;
import com.windowtester.runtime.gef.internal.identifier.ResizeHandleIdentifier;
import com.windowtester.runtime.gef.internal.identifier.SimpleClassBasedFigureIdentifier;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.PaletteButtonLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.Context;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;

/**
 * Figure Identifier Service.
 */
public class FigureIdentifierService implements IFigureIdentifier {

	
	//setup proposal registry (NOTE: order determines rank)
	private final FigureLocatorProposerList proposers = new FigureLocatorProposerList();
	{
		//note these are ranked first
		proposers.add(new NamedFigureIdentifier());
		proposers.add(new NamedEditPartFigureIdentifier(getPartMapper()));
		//and these are ranked after names are tested...
		proposers.addIfAdaptable(new PaletteItemLocator(null /* ignored */));
		proposers.addIfAdaptable(new PaletteButtonLocator());
		proposers.add(new AnchorIdentifier().withDelegate(new SimpleClassBasedFigureIdentifier()));
		proposers.add(new ResizeHandleIdentifier().withDelegate(new SimpleClassBasedFigureIdentifier()));
		proposers.add(new SimpleClassBasedFigureIdentifier());
		proposers.addIfAdaptable(new FigureCanvasXYLocator(-1, -1 /* ignored */));
	}
	
	private static final IFigureIdentifier DEFAULT = new FigureIdentifierService();
	

	protected IGEFPartMapper getPartMapper() {
		return GEFFinder.getDefault();
	}
	
	public static IFigureIdentifier getDefault() {
		return DEFAULT;
	}
	
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure)
	 */
	public ILocator identify(IFigure figure, Event event) {
		//TODO: need elaboration loop here? <-- elaboration loop inside proposer...
		IEditorLocator editor  = findScopeLocator(figure, event);
		ILocator detail        = findDetailLocator(figure, event);
		return attachScope(detail, editor);
	}


	private ILocator attachScope(ILocator detail, IEditorLocator editor) {
		//TODO: figure this out!	
		return detail;
	}


	protected IEditorLocator findScopeLocator(IFigure figure, Event event) {
		figure = fixNullCase(figure, event);
		return Draw2DFinder.getDefault().findEditorLocator(Context.GLOBAL.getUI(), figure);
	}

	
	private IFigure fixNullCase(IFigure figure, Event event) {
		if (figure != null)
			return figure;
		Widget widget = event.widget;
		if (!(widget instanceof FigureCanvas))
			return null;
		FigureCanvas canvas = (FigureCanvas)widget;
		return canvas.getContents();
	}


	protected ILocator findDetailLocator(IFigure figure, Event event) {
		//TODO: this use of scope isn't quite right...
		return getProposers().identify(UnscopedSearch.getInstance(), figure, event);
	}

	private FigureLocatorProposerList getProposers() {
		return proposers;
	}
	

	
}
