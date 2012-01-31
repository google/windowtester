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
package com.windowtester.runtime.gef.internal.finder.scope;

import java.io.Serializable;

import org.eclipse.swt.widgets.Control;

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.internal.finder.BasicWidgetFinder;
import com.windowtester.runtime.internal.finder.scope.IWidgetSearchScope;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * View-bound search scope.
 */
public class ViewScope extends AbstractScope implements IFigureSearchScope, IWidgetSearchScope, Serializable {

	private static final long serialVersionUID = 5518744998066826029L;

	private final ViewLocator viewLocator;

	public ViewScope(ViewLocator viewLocator) {
		Invariants.notNull(viewLocator);
		this.viewLocator = viewLocator;
	}
	
	public ViewLocator getViewLocator() {
		return viewLocator;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.gef.IFigureMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IFigureMatcher matcher) {
		Control viewControl = getViewControl();	
		if (viewControl == null)
			return noMatches();
		return new WidgetScope(viewControl).findAll(ui, matcher);
	}

	protected Control getViewControl() {
		return ViewFinder.getViewControl(getViewLocator().getViewId());
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.finder.scope.IWidgetSearchScope#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IWidgetMatcher matcher) {
		Control viewControl = getViewControl();
		if (viewControl == null)
			return noMatches();
		return new BasicWidgetFinder().findAllLocators(viewControl, matcher);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.scope.AbstractScope#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == IWidgetLocator.class)
			return getViewLocator();
		return super.getAdapter(adapter);
	}
	
}
