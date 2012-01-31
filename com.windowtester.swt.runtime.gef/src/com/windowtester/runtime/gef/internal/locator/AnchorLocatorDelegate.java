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
package com.windowtester.runtime.gef.internal.locator;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;

import com.windowtester.internal.runtime.locator.IAdaptableWidgetLocator;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.finder.AnchorFinder;
import com.windowtester.runtime.gef.locator.IFigureLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.internal.matchers.InstanceMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * The heavy lifter for anchor locators.
 */
public class AnchorLocatorDelegate extends FigureLocatorDelegate {

	private static final long serialVersionUID = 4649927492713703353L;


	//sentinel
	private static class HostNotFoundLocator implements IWidgetReference {

		private final WidgetSearchException e;
		public HostNotFoundLocator(WidgetSearchException e) {
			this.e = e;
		}
		public IWidgetLocator[] findAll(IUIContext ui) {
			return new IWidgetLocator[]{};
		}
		public boolean matches(Object widget) {
			return false;
		}
		public Object getWidget() {
			return null;
		}
		
		public void rethrowException() throws WidgetSearchException {
			throw e;
		}
	}
	
	
	private static class FigureCanvasInstanceLocator extends SWTWidgetLocator {

		private static final long serialVersionUID = 1L;
		private final FigureCanvas canvas;
		
		FigureCanvasInstanceLocator(FigureCanvas canvas) {
			super(FigureCanvas.class);
			this.canvas = canvas;			
		}
		
		protected ISWTWidgetMatcher buildMatcher() {
			return new InstanceMatcher(canvas);
		}
		
	}
	
	
	
	private final Position position;
	private final IFigureLocator hostFigureLocator;

	public AnchorLocatorDelegate(Position position, IFigureLocator hostFigureLocator) {
		super(FigureMatcherAdapter.forLocator(hostFigureLocator));
		this.position = position;
		this.hostFigureLocator = hostFigureLocator;
	}


	public ConnectionAnchor findAnchor(IUIContext ui) throws WidgetSearchException {
		IFigureReference host = getHost(ui);
		return AnchorFinder.forPositionInFigure(position, host.getFigure()).getAnchor();
	}


	private IFigureReference getHost(IUIContext ui) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		IWidgetLocator[] host = hostFigureLocator.findAll(ui);
		if (host.length == 0)
			throw new WidgetNotFoundException("host figure for anchor not found: " + hostFigureLocator);
		if (host.length > 1)
			throw new MultipleWidgetsFoundException("multiple hosts found for anchor: " + hostFigureLocator);
		return (IFigureReference) host[0];
	}
	
	
	public static IAdaptableWidgetLocator forPositionRelativeToHost(Position position, IFigureLocator hostFigure) {
		return new AnchorLocatorDelegate(position, hostFigure);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.locator.AbstractFigureLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		if (widget instanceof HostNotFoundLocator)
			((HostNotFoundLocator)widget).rethrowException();
		
		AnchorReference anchor = (AnchorReference)widget;
		return doClick(ui, anchor);
	}
	
	
	private IWidgetLocator doClick(IUIContext ui, AnchorReference anchorRef) throws WidgetSearchException {
		ConnectionAnchor anchor = anchorRef.getAnchor();
		Point pt = anchor.getReferencePoint();
		FigureCanvas canvas = Draw2DFinder.getDefault().findParentCanvas(ui, anchor.getOwner());
		return ui.click(new XYLocator(new FigureCanvasInstanceLocator(canvas), pt.x, pt.y ));
	}


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.locator.AbstractFigureLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		try {
			return new IWidgetLocator[]{AnchorReference.forAnchor(findAnchor(ui))};
		} catch (WidgetSearchException e) {
			return new IWidgetLocator[]{new HostNotFoundLocator(e)};
		}
	}
	

}
