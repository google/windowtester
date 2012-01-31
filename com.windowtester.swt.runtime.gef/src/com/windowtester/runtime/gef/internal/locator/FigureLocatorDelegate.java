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
package com.windowtester.runtime.gef.internal.locator;

import org.eclipse.jface.viewers.ILabelProvider;

import com.windowtester.internal.runtime.finder.IIdentifierHintProvider;
import com.windowtester.internal.runtime.finder.ISearchScope;
import com.windowtester.internal.runtime.locator.IAdaptableWidgetLocator;
import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.internal.runtime.locator.IUISelector2;
import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.draw2d.internal.locator.AbstractFigureLocator;
import com.windowtester.runtime.draw2d.internal.selectors.FigureSelectorDelegate;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.internal.finder.IFigureFinder;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.internal.finder.StateAwareFigureFinder;
import com.windowtester.runtime.gef.internal.finder.scope.UnscopedSearch;
import com.windowtester.runtime.gef.locator.FigureLocator;

/**
 * Does the heavy lifting in figure finding and selecting.  Essentially
 * this class exists to encapsulate the internal bits that the
 * API exposed {@link FigureLocator} needs to leverage.
 *
 */
public class FigureLocatorDelegate extends AbstractFigureLocator implements IAdaptableWidgetLocator, IIdentifierHintProvider {

	private static final long serialVersionUID = 8336263251739518791L;
	
	private IFigureMatcher _matcher;
	private IFigureSearchScope _scope;
	
	//finder (lazily initialized)
	private transient IFigureFinder _finder = null;
	
	//selector (lazily initialized)
	private transient IUISelector2 _selector = null;
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Instance creation
	//
	///////////////////////////////////////////////////////////////////////////////
		
	/**
	 * Create an instance parameterized by the given matcher.
	 */
	public FigureLocatorDelegate(IFigureMatcher matcher) {
		this(matcher, UnscopedSearch.getInstance()); //a "null" object that uses an unscoped search strategy
	}
	
	/**
	 * Create an instance parameterized by the given matcher.
	 */
	public FigureLocatorDelegate(IFigureMatcher matcher, IFigureSearchScope scope) {
		setMatcher(matcher);
		setScope(scope);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Parameterization
	//
	///////////////////////////////////////////////////////////////////////////////
		
	/**
	 * Set the matcher.
	 * @param matcher
	 */
	protected final void setMatcher(IFigureMatcher matcher) {
		Invariants.notNull(matcher);
		_matcher = matcher;
	}
	
	/**
	 * Set the search scope.
	 */
	protected final void setScope(IFigureSearchScope scope) {
		Invariants.notNull(scope);
		//TODO: we might consider making these composeable...
		_scope = scope;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Default/overridable behavior
	//
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Create the finder for use by this locator.
	 * <p>
	 * Subclasses may override.
	 */
	protected IFigureFinder createFinder() {
		return new StateAwareFigureFinder(getSearchScope());
	}

	/**
	 * Create the selector for use by this locator.
	 * <p>
	 * Subclasses may override.
	 */
	protected IUISelector2 createSelector() {
		return new FigureSelectorDelegate();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	///////////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.locator.AbstractFigureLocator2#getMatcher()
	 */
	protected final IFigureMatcher getMatcher() {
		return _matcher;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.internal.finder.IFigureSearchScopeable#getSearchScope()
	 */
	public final IFigureSearchScope getSearchScope() {
		return _scope;
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.locator.AbstractFigureLocator2#getSelector()
	 */
	protected final IUISelector getSelector() {
		if (_selector == null) {
			_selector = createSelector();
			if (_selector == null)
				throw new IllegalStateException("createSelector must not return a null selector");
		}
		return _selector;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.locator.AbstractFigureLocator2#getFinder()
	 */
	protected final IFigureFinder getFinder() {
		if (_finder == null) {
			_finder = createFinder();
			if (_finder == null)
				throw new IllegalStateException("createFinder must not return a null finder");
		}
		return _finder;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Extension services
	//
	///////////////////////////////////////////////////////////////////////////////
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.FigureLocator#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == ILabelProvider.class)
			return FigureLabelProvider.forDelegate(this);
		if (adapter == IIdentifierHintProvider.class)
			return this;
		if (adapter == ISearchScope.class)
			return getSearchScope();
		if (adapter == IFigureIdentifier.class)
			return getFigureIdentifier();
		if (adapter == IUISelector.class)
			return this;
		if (adapter == IUISelector2.class)
			return getSelector();
		if (adapter == IFigureMatcher.class)
			return getMatcher();
		return null;
	}

	/**
	 * Default returns null.  Override to provide figure identification services.
	 */
	protected IFigureIdentifier getFigureIdentifier() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.finder.IIdentifierHintProvider#requiresXY()
	 */
	public boolean requiresXY() {
		// TODO Auto-generated method stub
		return false;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Debugging
	//
	///////////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "FigureLocator(" + getMatcher() + ")";
	}

	
	
	
	
	
}
