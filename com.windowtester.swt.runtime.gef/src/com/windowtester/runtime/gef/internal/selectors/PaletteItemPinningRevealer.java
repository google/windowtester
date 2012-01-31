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

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.locator.PaletteButtonLocator;

/**
 * A revealer for palette items.
 */
public class PaletteItemPinningRevealer extends AbstractPaletteRevealer {

	public PaletteItemPinningRevealer(IPaletteViewerProvider viewerProvider, IEditPartProvider partProvider) {
		super(viewerProvider, partProvider);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.selectors.AbstractPaletteRevealer#doRevealPalette(com.windowtester.runtime.IUIContext)
	 */
	public void doRevealPalette(IUIContext ui) throws WidgetSearchException {
		ui.click(new PaletteButtonLocator());
	}


	
}
