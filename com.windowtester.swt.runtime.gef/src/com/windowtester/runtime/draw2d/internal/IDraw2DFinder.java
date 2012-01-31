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
package com.windowtester.runtime.draw2d.internal;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;


public interface IDraw2DFinder {


	IFigure[] findAllFigures(IUIContext ui, IFigureMatcher matcher);

	IWidgetLocator[] findAllFigureLocators(IUIContext ui, IFigureMatcher matcher);

	IWidgetLocator[] findAllFigureLocators(IUIContext ui, FigureCanvas root, IFigureMatcher matcher);

	IWidgetLocator[] findAllFigureLocators(IFigure rootFigure, IFigureMatcher matcher);

	IFigureReference[] findAllFigureReferences(IUIContext ui, IFigureMatcher matcher);
	
	FigureCanvas[] findAllCanvases(IUIContext ui);

	IFigure[] findAllFigures(FigureCanvas canvas, IFigureMatcher matcher);
	
	IFigure[] findAllFigures(IFigure root, IFigureMatcher matcher);

	
	FigureCanvas findParentCanvas(IUIContext ui, IFigure target);
	
	/**
	 * Find the first figure from this figure root that matches the given criteria.
	 */
	IFigure findFirstFigure(IFigure figureRoot, IFigureMatcher figureMatcher);


	IEditorLocator findEditorLocator(IUIContext ui, IFigure figure);

	boolean isContainedIn(IFigure figure, IFigureMatcher parentMatcher);

	IFigure findSibling(IFigure figure, IFigureMatcher matcher);


	
	
}
