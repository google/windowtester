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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.internal.finder.BasicWidgetFinder;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * Scope figure search by a given widget.
 */
public class WidgetScope implements IFigureSearchScope {

	
	private final Widget _root;

	public WidgetScope(Widget root) {
		_root = root;
	}

	public Widget getWidgetRoot() {
		return _root;
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.gef.IFigureMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IFigureMatcher matcher) {
		
		FigureCanvas[] canvas = findCanvases();
		
		List found = new ArrayList();
		
		for (int i = 0; i < canvas.length; i++) {
			IWidgetLocator[] matches = Draw2DFinder.getDefault().findAllFigureLocators(ui, canvas[i], matcher);
			for (int j = 0; j < matches.length; j++) {
				found.add(matches[j]);
			}
		}
		return (IWidgetLocator[]) found.toArray(new IWidgetLocator[]{});
	}

	private FigureCanvas[] findCanvases() {
		
		Widget[] widgets = new BasicWidgetFinder().findAll(getWidgetRoot(), new SWTWidgetLocator(FigureCanvas.class));
		
		FigureCanvas[] canvases = new FigureCanvas[widgets.length];
		for (int i = 0; i < canvases.length; i++) {
			canvases[i] = (FigureCanvas)widgets[i];
		}
		return canvases;
	}



}
