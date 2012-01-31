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
package com.windowtester.runtime.draw2d.internal.locator;

import java.io.Serializable;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.commandstack.CommandStackTransaction;
import com.windowtester.runtime.gef.internal.commandstack.UIRunnable;
import com.windowtester.runtime.gef.internal.finder.IFigureFinder;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * An abstract locator designed for subclassing.
 */
public abstract class AbstractFigureLocator implements IWidgetLocator, IUISelector, Serializable {

		
	private static final long serialVersionUID = -4604661364920402361L;

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return getFinder().findAll(ui, getMatcher());
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		if (!(widget instanceof IFigureReference))
			return false;
		return getMatcher().matches((IFigureReference)widget);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(final IUIContext ui, final IWidgetReference widget, final IClickDescription click) throws WidgetSearchException {
		return (IWidgetLocator) CommandStackTransaction.forActiveEditor().runInUI(new UIRunnable() {
			public Object runWithResult() throws WidgetSearchException {
				return getSelector().click(ui, widget, click);
			}
		}, ui);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(final IUIContext ui, final IWidgetReference widget, final IClickDescription click, final String menuItemPath) throws WidgetSearchException {
		return (IWidgetLocator) CommandStackTransaction.forActiveEditor().runInUI(new UIRunnable() {
			public Object runWithResult() throws WidgetSearchException {
				return getSelector().contextClick(ui, widget, click, menuItemPath);
			}
		}, ui);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Hooks
	//
	/////////////////////////////////////////////////////////////////////////////////
	
	protected abstract IFigureFinder getFinder();
	
	protected abstract IUISelector getSelector();
	
	protected abstract IFigureMatcher getMatcher();


}
