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

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.internal.selectors.FigureSelectorDelegate;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * A template class for revealing delegates.
 */
public abstract class RevealingFigureSelectorDelegate extends FigureSelectorDelegate implements IRevealer {

	
	/* 
	 * (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public final IWidgetLocator click(final IUIContext ui, final IWidgetReference widget,
			final IClickDescription click) throws WidgetSearchException {
		doReveal(ui);
		return doClick(ui, widget, click);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public final IWidgetLocator contextClick(final IUIContext ui, final IWidgetReference widget,
			final IClickDescription click, final String menuItemPath) throws WidgetSearchException {
		doReveal(ui);
		return doContextClick(ui, widget, click, menuItemPath);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.selectors.IRevealer#reveal(com.windowtester.runtime.IUIContext)
	 */
	public final void reveal(final IUIContext ui) throws WidgetSearchException {
		//NOTE: retries happen further upstream
		doReveal(ui);
	}
	
	public IWidgetLocator doContextClick(IUIContext ui,
			IWidgetReference widget, IClickDescription click,
			String menuItemPath) throws WidgetSearchException {
		return super.contextClick(ui, widget, click, menuItemPath);
	}

	public IWidgetLocator doClick(IUIContext ui, IWidgetReference widget,
			IClickDescription click) throws WidgetSearchException {
		return super.click(ui, widget, click);
	}

	public abstract void doReveal(IUIContext ui) throws WidgetSearchException;
}
