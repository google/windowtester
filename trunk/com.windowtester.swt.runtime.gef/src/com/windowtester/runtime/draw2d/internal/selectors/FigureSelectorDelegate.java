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
package com.windowtester.runtime.draw2d.internal.selectors;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.windowtester.internal.runtime.ISelectionTarget;
import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.internal.runtime.locator.IUISelector2;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.draw2d.internal.helpers.FigureSelectorHelper;
import com.windowtester.runtime.gef.internal.FigureReference;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.UIContextSWT;
import com.windowtester.runtime.swt.internal.dnd.DragAndDropHelper;

/**
 * Does the heavy lifting in figure clicks.
 */
public class FigureSelectorDelegate implements IUISelector2, IAdaptable {

	private FigureSelectorHelper selector;
	private DragAndDropHelper dndHelper;
	
	
	protected FigureSelectorHelper getSelector(IUIContext ui) {
		if (selector == null)
			selector = new FigureSelectorHelper(ui);
		return selector;
	}
	
	protected DragAndDropHelper getDNDHelper(IUIContext ui) {
		if (dndHelper == null)
			dndHelper = new DragAndDropHelper((UIContextSWT)ui); //sigh... this cast necessitated by legacy impl.
		return dndHelper;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.windowtester.internal.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext,
	 *      com.windowtester.runtime.locator.WidgetReference,
	 *      com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		IFigure figure = (IFigure) widget.getWidget();
		FigureSelectorHelper selectorHelper = getSelector(ui);
		Point clickPoint = ClickTranslator.makeRelativeToCenter(click, figure);
		selectorHelper.clickFigure(click.clicks(), figure, clickPoint.x, clickPoint.y);
		return createReference(figure);
	}



	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, IClickDescription click, String menuItemPath) throws WidgetSearchException {
		
		IFigure figure = (IFigure) widget.getWidget();
		FigureSelectorHelper selectorHelper = getSelector(ui);
		if (click.isDefaultCenterClick())
			selectorHelper.contextClickFigure(figure, menuItemPath);
		else
			selectorHelper.contextClickFigure(figure, click.x(), click.y(), menuItemPath);
		
		//TODO: refactor helper to return clicked figure?
		return createReference(figure);
	}


	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector2#mouseMove(com.windowtester.internal.runtime.ISelectionTarget)
	 */
	public IWidgetLocator mouseMove(IUIContext ui, ISelectionTarget target) throws WidgetSearchException {
		IWidgetLocator locator = target.getWidgetLocator();
		IWidgetReference ref   = (IWidgetReference) ui.find(locator);
		IFigure figure = (IFigure) ref.getWidget();		
		getSelector(ui).mouseMove(figure, target.getClickDescription());
		return ref;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector2#dragTo(com.windowtester.runtime.IUIContext, com.windowtester.internal.runtime.ISelectionTarget)
	 */
	public IWidgetLocator dragTo(IUIContext ui, ISelectionTarget target) throws WidgetSearchException {
		IWidgetReference figureRef = (IWidgetReference) ui.find(target.getWidgetLocator());
		IFigure figure = (IFigure) figureRef.getWidget();	
		
		FigureCanvas canvas = Draw2DFinder.getDefault().findParentCanvas(ui, figure);
		FigureSelectorHelper selector = getSelector(ui);
		
		// Get the draw2d bounds and convert them to eclipse SWT screen
		// coordinates
		Rectangle figureBounds = selector.toFigureCanvas(figure);
		org.eclipse.draw2d.geometry.Rectangle bounds = new org.eclipse.draw2d.geometry.Rectangle(
				figureBounds.x, figureBounds.y, figureBounds.width,
				figureBounds.height);
		
		adjustBoundsWithClickDetails(bounds, target.getClickDescription());

		selector.scrollForClick(canvas, bounds);
		
		getDNDHelper(ui).dragTo(canvas, bounds.x, bounds.y);
		
		return figureRef;

	}
	
	
	private void adjustBoundsWithClickDetails(org.eclipse.draw2d.geometry.Rectangle bounds, IClickDescription click) {
		if (click.isDefaultCenterClick()) {
			org.eclipse.draw2d.geometry.Point center = bounds.getCenter();
			bounds.x = center.x;
			bounds.y = center.y;
			return;
		}
		bounds.x += click.x();
		bounds.y += click.y();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == IUISelector2.class)
			return this;
		if (adapter == IUISelector.class)
			return this;
		return null;
	}
	

	protected IWidgetLocator createReference(IFigure figure) {
		return FigureReference.create(figure);
	}

}
