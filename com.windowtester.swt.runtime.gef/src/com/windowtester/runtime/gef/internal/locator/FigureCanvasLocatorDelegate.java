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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Canvas;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.runtime.finder.IIdentifierHintProvider;
import com.windowtester.internal.runtime.finder.ISearchScope;
import com.windowtester.internal.runtime.locator.IAdaptableWidgetLocator;
import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.internal.helpers.FigureSelectorHelper;
import com.windowtester.runtime.gef.internal.FigureReference;
import com.windowtester.runtime.gef.internal.commandstack.CommandStackTransaction;
import com.windowtester.runtime.gef.internal.commandstack.UIRunnable;
import com.windowtester.runtime.internal.finder.scope.IWidgetSearchScope;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;
import com.windowtester.runtime.swt.internal.widgets.CanvasReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.IWorkbenchPartLocator;

/**
 * A locator for the Figure Canvas in a GEF Graphical Editor.
 */
public class FigureCanvasLocatorDelegate extends SWTWidgetLocator implements IAdaptableWidgetLocator, IIdentifierHintProvider {

	private static final long serialVersionUID = -526717330328663732L;
	
	private static boolean canGetFigureOwner = true;

	private final IWidgetSearchScope _scope;
	
	public FigureCanvasLocatorDelegate() {
		this(ScopeFactory.unspecifedEditorLocator());
	}
	
	public FigureCanvasLocatorDelegate(String editorName) {
		this(ScopeFactory.editorLocator(editorName));
	}
	
	public FigureCanvasLocatorDelegate(IWorkbenchPartLocator partLocator) {
		super(FigureCanvas.class);
		_scope = ScopeFactory.widgetScopeForPart(partLocator);
	}	

	/**
	 * Get the search scope for this canvas.
	 */
	protected final IWidgetSearchScope getScope() {
		return _scope;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public final IWidgetLocator[] findAll(IUIContext ui) {
		return getScope().findAll(ui, getMatcher());
	}


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.FigureLocator#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == ILabelProvider.class)
			return new FigureLabelProvider();
		if (adapter == IUISelector.class)
			return this;
		if (adapter == ISearchScope.class)
			return getScope();
		return null;
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.finder.IIdentifierHintProvider#requiresXY()
	 */
	public boolean requiresXY() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Override the superclass implementation to return a {@link FigureReference} rather
	 * than the canvas itself
	 */
	public IWidgetLocator click(final IUIContext ui, final IWidgetReference widget, final IClickDescription click)
		throws WidgetSearchException {
		return (IWidgetLocator) CommandStackTransaction.forActiveEditor().runInUI(new UIRunnable() {
			public Object runWithResult() throws WidgetSearchException {
				return doClick(ui, widget, click);
			}
		}, ui);
	}

	private IWidgetLocator doClick(IUIContext ui, IWidgetReference widget,
			IClickDescription click) throws WidgetSearchException {
		
		
		FigureCanvas canvas = (FigureCanvas) widget.getWidget();
		//TODO: preClick and postClick need to use the adjusted x and y post scroll
		//preClick(canvas, new Point(click.x(), click.y()), ui);
		new FigureSelectorHelper(ui).clickPoint(canvas, click);
		//postClick(canvas, ui);
		
		//WidgetReference canvasRef = (WidgetReference) super.click(ui, widget, click);
		//FigureCanvas canvas = (FigureCanvas) canvasRef.getWidget();
				
		return resolveReferenceForClick(click, canvas);
	}


	private IWidgetLocator doContextClick(IUIContext ui, final IWidgetReference widget, final IClickDescription click, String menuItemPath) throws WidgetSearchException {
		
//		//NOTE: preClick and postClick need to use the adjusted x and y post scroll
//		FigureCanvas canvas = (FigureCanvas) widget.getWidget();
//		//preClick(canvas, new Point(click.x(), click.y()), ui);
//		new FigureSelectorHelper(ui).contextClickPoint(canvas, click, menuItemPath);
//		//postClick(canvas, ui);
//		return resolveReferenceForClick(click, canvas);

		/*
		 * TODO: this should be pushed into a proper FigureReference class implementation.
		 */		
		return new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				FigureCanvas canvas = (FigureCanvas) widget.getWidget();
				SWTLocation location = SWTWidgetLocation.withDefaultCenter(new CanvasReference<Canvas>(canvas), click);
				SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON3, location, false);
				op.execute();
				return op.getMenu();
			}
		}, menuItemPath);
	}
	
	private IWidgetLocator resolveReferenceForClick(IClickDescription click,
			final FigureCanvas canvas) {
		
		IFigure figure = determineFigureToClick(click, canvas);

		// If the figure is a handle, then determine the figure that owns it
		if (canGetFigureOwner && figure instanceof AbstractHandle) {
			Object result = null;
			try {
				result = getOwnerFigure(figure);
			}
			catch (Exception e) {
				canGetFigureOwner = false;
				Logger.log("Failed to access AbstractHandle.getOwnerFigure()", e);
			}
			if (result != null)
				figure = ((IFigure) result);
		}
		
		// If a figure was located, then return a reference to that figure
		if (figure != null)
			return FigureReference.lazy(figure);
		
		// If nothing else, return the canvas reference
		return 	WidgetReference.create(canvas, this);
	
	}

	private Object getOwnerFigure(IFigure figure) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		Method method = AbstractHandle.class.getDeclaredMethod("getOwnerFigure", new Class[] {});
		method.setAccessible(true);
		return method.invoke(figure, new Object[] {});
	}

	private IFigure determineFigureToClick(IClickDescription click,
			FigureCanvas canvas) {
		IFigure contents = canvas.getContents();
		IFigure figure   = contents.findFigureAt(click.x(), click.y());
		return figure;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(final IUIContext ui, final IWidgetReference widget,
			final IClickDescription click, final String menuItemPath)
			throws WidgetSearchException {
		return (IWidgetLocator) CommandStackTransaction.forActiveEditor().runInUI(new UIRunnable() {
			public Object runWithResult() throws WidgetSearchException {
				return doContextClick(ui, widget, click, menuItemPath);
			}
		}, ui);
	}

	
}
