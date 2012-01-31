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
package com.windowtester.runtime.gef.internal;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

import com.windowtester.internal.runtime.ClassReference;
import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.internal.locator.Draw2DWidgetReference;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

/**
 * Basic FigureReference implementation.
 */
public class FigureReference implements IFigureReference, IWidgetReference, IAdaptable {

	/**
	 * Create a figure info instance.  Note that by default this
	 * info object is lazily populated.  If you need an eagerly populated
	 * info object, use the 
	 * @param figure
	 * @return
	 */
	public static FigureReference create(IFigure figure) {
		return lazy(figure);
	}

	public static FigureReference eager(IFigure figure) {
		return new FigureReference(figure, FigureInfoState.eager(figure));
	}
	
	public static FigureReference lazy(IFigure figure) {
		return new FigureReference(figure, FigureInfoState.lazy(figure));
	}
	
	private final IFigure _figure;
	private final ClassReference _class;
	private final FigureInfoState _state; //TODO: inline state
	
	private final IWidgetLocator _locatorDelegate;
	
	
	protected FigureReference(IFigure figure, FigureInfoState state) {
		Invariants.notNull(figure);
		Invariants.notNull(state);
		
		_figure          = figure;
		_class           = new ClassReference(figure.getClass());
		_state           = state;
		_locatorDelegate = Draw2DWidgetReference.create(figure);
	}

	protected FigureInfoState getState() {
		return _state;
	}
	
	protected IWidgetLocator getLocatorDelegate() {
		return _locatorDelegate;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureReference#getFigure()
	 */
	public IFigure getFigure() {
		return _figure;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureReference#getEditPart()
	 */
	public EditPart getEditPart() {
		return getState().getEditPart().getEditPart();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureReference#getChildren()
	 */
	public IFigureReference[] getChildren() {
		return getState().getChildren().toArray();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureReference#getParent()
	 */
	public IFigureReference getParent() {
		return getState().getParent();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureReference#getConnections()
	 */
	public IConnectionInfo[] getConnections() {
		return getState().getConnections().toArray();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetReference#getWidget()
	 */
	public IFigure getWidget() {
		/*
		 * NOTE: this is not terribly clean but we need to adapt to IWidgetReference
		 * in order to play nice with our legacy finder and click helper.
		 */
		return getFigure();
	}
	
	
	protected void accept(IFigureInfoVisitor visitor) {
		visitor.visit(this);
		
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureReference#getType()
	 */
	public ClassReference getType() {
		return _class;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return getLocatorDelegate().findAll(ui);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return getLocatorDelegate().matches(widget);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		IWidgetLocator locatorDelegate = getLocatorDelegate();
		if (!(locatorDelegate instanceof IAdaptable))
			return null;
		return ((IAdaptable)locatorDelegate).getAdapter(adapter);
	}
	
	
}
