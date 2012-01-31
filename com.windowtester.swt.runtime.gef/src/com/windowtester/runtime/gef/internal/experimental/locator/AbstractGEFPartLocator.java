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
package com.windowtester.runtime.gef.internal.experimental.locator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.draw2d.internal.helpers.FigureHelper;
import com.windowtester.runtime.draw2d.internal.locator.Draw2DWidgetReference;
import com.windowtester.runtime.draw2d.internal.selectors.FigureSelectorDelegate;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;
import com.windowtester.runtime.gef.internal.IGEFEditPartReference;
import com.windowtester.runtime.gef.internal.IGEFPartLocator;
import com.windowtester.runtime.gef.internal.helpers.EditPartReferenceResolver;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;

/**
 * A locator for GEF {@link EditPart}s.
 */
public abstract class AbstractGEFPartLocator implements IWidgetLocator, IUISelector, IGEFPartLocator {

	private IGEFEditPartMatcher _matcher;
	private EditPartReferenceResolver _resolver;
	private EditorLocator _editorLocator;
	private IFigureMatcher _primaryFigureMatcher;
	private transient IUISelector _selector;
	
	protected final IUISelector getSelector(IUIContext ui) {
		if (_selector == null)
			_selector = new FigureSelectorDelegate();
		return _selector;
	}
	
	public final IGEFEditPartMatcher getPartMatcher() {
		if (_matcher == null)
			_matcher = buildMatcher();
		return _matcher;
	}
	
	public final IFigureMatcher getFigureMatcher() {
		if (_primaryFigureMatcher == null)
			_primaryFigureMatcher = buildPrimaryFigureMatcher();
		return _primaryFigureMatcher;
	}
	
	public final EditorLocator getEditorLocator() {
		if (_editorLocator == null)
			_editorLocator = buildViewerLocator();
		return _editorLocator;
	}
	
	
		
	/**
	 * Build the associated matcher (to be cached).
	 */
	protected abstract IGEFEditPartMatcher buildMatcher();
	
	/**
	 * Build a locator to identify the target editor (to be cached).
	 */
	protected abstract EditorLocator buildViewerLocator();
	
	
	/**
	 * Build a matcher to identify the primary figure (the one to click) associated with this part.  The figure tree
	 * rooted by {@link GraphicalEditPart#getFigure()} is visited and the first match is selected.
	 * <p>
	 * The default implementation matches the topmost figure.  Override if other behavior is appropriate.
	 */
	protected IFigureMatcher buildPrimaryFigureMatcher() {
		return new IFigureMatcher() {
			public boolean matches(IFigureReference figure) {
				return true;
			}
		};
	}
	
	
	private EditPartReferenceResolver getResolver() {
		if (_resolver == null)
			_resolver = new EditPartReferenceResolver();
		return _resolver;
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return getResolver().findAll(this);
	}

	/** 
	 * Tests if the given {@link EditPart} matches our match criteria.
	 * 
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object part) {
		if (!(part instanceof EditPart))
			return false;
		return getPartMatcher().matches((EditPart) part);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget, IClickDescription click) throws WidgetSearchException {
		IFigure figure = getPrimaryFigure(widget);
		return doClick(ui, figure, click);
	}

	private IFigure getPrimaryFigure(IWidgetReference widget) throws WidgetNotFoundException {
		IGEFEditPartReference partRef = (IGEFEditPartReference)widget;
		GraphicalEditPart part = partRef.getPart();
		IFigure figure = getPrimaryFigure(part.getFigure());
		return figure;
	}
		
	private IWidgetLocator doClick(IUIContext ui, IFigure figure, IClickDescription click) throws WidgetSearchException {
		//TODO: this re-wrappering seems silly... but the current API requires it
		return getSelector(ui).click(ui, adaptToReference(figure), click);
	}

	private IWidgetReference adaptToReference(IFigure figure) {
		return (IWidgetReference)Draw2DWidgetReference.create(figure);
	}

	private IFigure getPrimaryFigure(IFigure figureRoot) throws WidgetNotFoundException {
		IFigure figure = Draw2DFinder.getDefault().findFirstFigure(figureRoot, getFigureMatcher());
		if (figure == null)
			throw new WidgetNotFoundException("Matcher: " + getFigureMatcher() + " matched no figures in " + FigureHelper.toString(figureRoot));
		return figure;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, IClickDescription click, String menuItemPath) throws WidgetSearchException {
		IFigure figure = getPrimaryFigure(widget);
		return doContextClick(ui, figure, click, menuItemPath);
	}

	private IWidgetLocator doContextClick(IUIContext ui, IFigure figure, IClickDescription click, String menuItemPath) throws WidgetSearchException {
		return getSelector(ui).contextClick(ui, adaptToReference(figure), click, menuItemPath);
	}
	
}
