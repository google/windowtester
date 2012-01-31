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

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * A selector for selecting items in a GEF Palette.
 *
 */
public class PaletteItemSelector extends RevealingFigureSelectorDelegate implements IRevealer {

	private final PaletteDrawerSelector drawerSelector;
	private final IRevealer paletteItemRevealer;
	private final String pathString;
		
	public PaletteItemSelector(IPaletteViewerProvider viewerProvider, IEditPartProvider partProvider, String pathString) {
		
		Invariants.notNull(viewerProvider);
		Invariants.notNull(partProvider);
		Invariants.notNull(pathString);
		
		this.pathString          = pathString;
		this.paletteItemRevealer = PaletteItemRevealer.getCurrent(viewerProvider, partProvider);
		this.drawerSelector      = new PaletteDrawerSelector(paletteItemRevealer);
	}

	public IRevealer getPaletteItemRevealer() {
		return paletteItemRevealer;
	}
	
	protected PaletteDrawerSelector getDrawerSelector() {
		return drawerSelector;
	}
		
	public IWidgetLocator doClick(IUIContext ui, IWidgetReference widget,
			IClickDescription click) throws WidgetSearchException {
		if (getDrawerSelector().isDrawer(widget))
			return getDrawerSelector().click(ui, widget, click);
		try {
			return super.doClick(ui, widget, click);
		} catch (WidgetSearchException e) {
			throw new WidgetNotFoundException("Palette item: \"" + pathString + "\" not found");
		}
	}

	public IWidgetLocator doContextClick(IUIContext ui,
			IWidgetReference widget, IClickDescription click,
			String menuItemPath) throws WidgetSearchException {
		if (getDrawerSelector().isDrawer(widget))
			return getDrawerSelector().contextClick(ui, widget, click, menuItemPath);
		return super.doContextClick(ui, widget, click, menuItemPath);
	}

	public void doReveal(IUIContext ui) throws WidgetSearchException {
		getPaletteItemRevealer().reveal(ui);
	}


}