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
package com.windowtester.runtime.draw2d.internal.finder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.internal.IDraw2DFinder;
import com.windowtester.runtime.draw2d.internal.helpers.FigureHelper;
import com.windowtester.runtime.draw2d.internal.helpers.FigureHelper.IFigureVisitor;
import com.windowtester.runtime.draw2d.internal.matchers.FigureInstanceMatcher;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.FigureReference;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder.EditorNotFoundException;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder.MultipleEditorsFoundException;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;

public class Draw2DFinder implements IDraw2DFinder {

	
	static class MatchAccumulatingFigureVisitor implements IFigureVisitor {
		private final IFigureMatcher _matcher;
		private final List _matches = new ArrayList();

		public MatchAccumulatingFigureVisitor(IFigureMatcher matcher) {
			_matcher = matcher;
		}

		public boolean visit(IFigure figure) {
//			System.out.println("testing " + figure + " against " + _matcher);
//			new GEFDebugHelper().printFigures(figure);
			if (_matcher.matches(getInfo(figure)))
				_matches.add(figure);
			return true;
		}
		
		public IFigure[] getMatches() {
			return (IFigure[]) _matches.toArray(new IFigure[]{});
		}
	}
	
	
	private static IFigure[] NO_FIGURES = new IFigure[]{};
	
	private static final IDraw2DFinder DEFAULT = new Draw2DFinder();
	
	public static IDraw2DFinder getDefault() {
		return DEFAULT;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IDraw2DFinder#findAllFigureLocators(org.eclipse.draw2d.IFigure, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IWidgetLocator[] findAllFigureLocators(IFigure rootFigure, IFigureMatcher matcher) {
		return adaptFiguresToLocators(findAllFigures(rootFigure, matcher));
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.internal.finder.IDraw2DFinder#findAllFigureLocators(com.windowtester.runtime.IUIContext, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IWidgetLocator[] findAllFigureLocators(IUIContext ui, IFigureMatcher matcher) {
		FigureCanvas[] canvas = findAllCanvases(ui);
		List figures = new ArrayList();
		for (int i = 0; i < canvas.length; i++) {
			add(figures, findAllFigures(canvas[i], matcher));
		}
		return adaptFiguresToLocators(figures);
	}
	
	public IFigureReference[] findAllFigureReferences(IUIContext ui, IFigureMatcher matcher) {
		FigureCanvas[] canvas = findAllCanvases(ui);
		List figures = new ArrayList();
		for (int i = 0; i < canvas.length; i++) {
			add(figures, findAllFigures(canvas[i], matcher));
		}
		return adaptFiguresToReferences(figures);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IDraw2DFinder#findParentCanvas(org.eclipse.draw2d.IFigure)
	 */
	public FigureCanvas findParentCanvas(IUIContext ui, IFigure target) {
		FigureCanvas[] canvas = findAllCanvases(ui);
		for (int i = 0; i < canvas.length; i++) {
			FigureCanvas c = canvas[i];
			IFigure[] matches = findAllFigures(c, new FigureInstanceMatcher(target));
			if (matches.length != 0)
				return c;
		}
		return null;
	}
	
	
	private static IWidgetLocator[] adaptFiguresToLocators(List figures) {
		//convert to locators
		List locators = new ArrayList();
		for (Iterator iter = figures.iterator(); iter.hasNext();) {
			locators.add(FigureReference.create((IFigure) iter.next()));
		}
		return (IWidgetLocator[]) locators.toArray(new IWidgetLocator[]{});
	}

	private static IFigureReference[] adaptFiguresToReferences(List figures) {
		//convert to locators
		List locators = new ArrayList();
		for (Iterator iter = figures.iterator(); iter.hasNext();) {
			locators.add(FigureReference.create((IFigure) iter.next()));
		}
		return (IFigureReference[]) locators.toArray(new IFigureReference[]{});
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.internal.finder.IDraw2DFinder#findFirstFigure(org.eclipse.draw2d.IFigure, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IFigure findFirstFigure(IFigure figureRoot, final IFigureMatcher matcher) {
		final IFigure[] match = new IFigure[1];
		FigureHelper.visit(figureRoot, new IFigureVisitor() {
			public boolean visit(IFigure figure) {
				if (match[0] == null && matcher.matches(getInfo(figure))) {
					match[0] = figure;
					return false;
				}
				return true;
			}
		});
		return match[0];
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.internal.finder.IDraw2DFinder#findAllFigureLocators(com.windowtester.runtime.IUIContext, org.eclipse.draw2d.FigureCanvas, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IWidgetLocator[] findAllFigureLocators(IUIContext ui, FigureCanvas root, IFigureMatcher matcher) {
		IFigure[] figures = findAllFigures(root, matcher);
		return adaptFiguresToLocators(figures);
	}
	
	private static IWidgetLocator[] adaptFiguresToLocators(IFigure[] figures) {
		List locators = new ArrayList();
		for (int i = 0; i < figures.length; i++) {
			locators.add(FigureReference.create((IFigure)figures[i]));
		}
		return (IWidgetLocator[]) locators.toArray(new IWidgetLocator[]{});
	}

	private static void add(List list, Object[] elems) {
		if (elems == null)
			return;
		for (int i = 0; i < elems.length; i++) {
			list.add(elems[i]);	
		}
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IDraw2DFinder#findAllFigures(com.windowtester.runtime.IUIContext, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IFigure[] findAllFigures(IUIContext ui, IFigureMatcher matcher) {
		List matches = new ArrayList();
		FigureCanvas[] canvas = findAllCanvases(ui);
		for(int i= 0; i < canvas.length; ++i) {
			IFigure[] figures = findAllFigures(canvas[i], matcher);
			for (int j = 0; j < figures.length; j++) {
				matches.add(figures[j]);
			}
		}
		return (IFigure[]) matches.toArray(new IFigure[]{});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IDraw2DFinder#findAllFigures(org.eclipse.draw2d.FigureCanvas, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IFigure[] findAllFigures(FigureCanvas canvas, IFigureMatcher matcher) {
		IFigure figure = canvas.getContents();
		return findAllFigures(figure, matcher);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IDraw2DFinder#findAllFigures(org.eclipse.draw2d.IFigure, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IFigure[] findAllFigures(IFigure root, IFigureMatcher matcher) {
		if (root == null)
			return NO_FIGURES;
		MatchAccumulatingFigureVisitor collector = new MatchAccumulatingFigureVisitor(matcher);
		FigureHelper.visit(root, collector);
		return collector.getMatches();
		
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IDraw2DFinder#findAllCanvases(com.windowtester.runtime.IUIContext)
	 */
	public FigureCanvas[] findAllCanvases(IUIContext ui) {
		IWidgetLocator[] found  = ui.findAll(new SWTWidgetLocator(FigureCanvas.class));
		FigureCanvas[] canvases = new FigureCanvas[found.length];
		for (int i = 0; i < canvases.length; i++) {
			canvases[i] = (FigureCanvas) ((IWidgetReference)found[i]).getWidget();
		}
		return canvases;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IDraw2DFinder#findEditorLocator(org.eclipse.draw2d.IFigure, com.windowtester.runtime.IUIContext)
	 */
	public IEditorLocator findEditorLocator(IUIContext ui, IFigure figure) {
		if (figure == null)
			return null;
		FigureCanvas canvas = findParentCanvas(ui, figure);
		if (canvas == null)
			throw new IllegalStateException("parent canvas is null");
		try {
			return EditorFinder.findContainingEditorLocator(canvas);
		} catch (EditorNotFoundException e) {
			return null;
		} catch (MultipleEditorsFoundException e) {
			throw new IllegalStateException("canvas owned by multiple editors");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IDraw2DFinder#isContainedIn(org.eclipse.draw2d.IFigure, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public boolean isContainedIn(IFigure figure, IFigureMatcher parentMatcher) {
		if (figure == null)
			return false;
		IFigure parent = figure.getParent();
		while (parent != null) {
			//System.out.println("testing parent: " + parent + " against matcher: " + parentMatcher); 
			if (parentMatcher.matches(getInfo(parent)))
				return true;
			//System.out.println("-> false");
			parent = parent.getParent();
		}
		return false;
	}

	private static IFigureReference getInfo(IFigure parent) {
		return FigureReference.create(parent);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IDraw2DFinder#findSibling(org.eclipse.draw2d.IFigure, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IFigure findSibling(IFigure figure, IFigureMatcher matcher) {
		IFigure parent = figure.getParent();
		if (parent == null)
			return null;
		List children = parent.getChildren();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			IFigure child = (IFigure) iter.next();
			if (child == figure)
				continue;
			if (matcher.matches(getInfo(child)))
				return child;
		}
		return null;
	}
	
	
}
