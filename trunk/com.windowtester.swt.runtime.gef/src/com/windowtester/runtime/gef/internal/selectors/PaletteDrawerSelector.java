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
package com.windowtester.runtime.gef.internal.selectors;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Toggle;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.internal.FigureReference;
import com.windowtester.runtime.gef.internal.matchers.PaletteItemPartMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * A specialized selector for handling drawers.
 */
public class PaletteDrawerSelector extends RevealingFigureSelectorDelegate {

	private final IRevealer _paletteItemRevealer;

	public PaletteDrawerSelector(IRevealer paletteItemRevealer) {
		_paletteItemRevealer = paletteItemRevealer;
	}

	public IRevealer getPaletteItemRevealer() {
		return _paletteItemRevealer;
	}

	/**
	 * Tests whether the given widget is a drawer reference.
	 */
	public boolean isDrawer(IWidgetReference widget) {
		IFigure figure = (IFigure) widget.getWidget();
		return PaletteItemPartMatcher.isDrawerFigure(figure);
	}
	
	
	protected IWidgetReference getClickableDrawerFigure(IWidgetReference widget) throws WidgetNotFoundException {
		IFigure figure = (IFigure) widget.getWidget();
		List children = figure.getChildren();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			IFigure child = (IFigure) iterator.next();
			if (child instanceof Toggle)
				return FigureReference.create(child);
		}
		throw new WidgetNotFoundException("drawer label not found");
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.selectors.RevealingFigureSelectorDelegate#doClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator doClick(IUIContext ui, IWidgetReference widget,
			IClickDescription click) throws WidgetSearchException {
		widget = getClickableDrawerFigure(widget);
		return super.doClick(ui, widget, click);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.selectors.RevealingFigureSelectorDelegate#doContextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator doContextClick(IUIContext ui,
			IWidgetReference widget, IClickDescription click,
			String menuItemPath) throws WidgetSearchException {
		widget = getClickableDrawerFigure(widget);
		return super.doContextClick(ui, widget, click, menuItemPath);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.selectors.RevealingFigureSelectorDelegate#doReveal(com.windowtester.runtime.IUIContext)
	 */
	public void doReveal(IUIContext ui) throws WidgetSearchException {
		getPaletteItemRevealer().reveal(ui);
	}
	
	
}
