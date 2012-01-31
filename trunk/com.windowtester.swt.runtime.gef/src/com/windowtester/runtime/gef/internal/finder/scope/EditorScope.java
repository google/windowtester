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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.widgets.Control;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;
import com.windowtester.runtime.gef.internal.finder.GEFFinder;
import com.windowtester.runtime.gef.internal.finder.IEditPartSearchScope;
import com.windowtester.runtime.gef.internal.helpers.EditorAccessor;
import com.windowtester.runtime.internal.finder.BasicWidgetFinder;
import com.windowtester.runtime.internal.finder.scope.IWidgetSearchScope;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;

/**
 * Editor-bound search scope.
 */
public class EditorScope extends AbstractScope implements IFigureSearchScope, IEditPartSearchScope, IWidgetSearchScope, Serializable {
	
	private static final long serialVersionUID = 8575580540868216617L;
	
	private final EditorAccessor editorAccessor;
	
	
	public EditorScope(IEditorLocator editorLocator) {
		editorAccessor = new EditorAccessor(editorLocator);
	}
	
	public EditorAccessor getEditor() {
		return editorAccessor;
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.internal.finder.IFigureSearchScope#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.draw2d.internal.IFigureMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IFigureMatcher matcher) {
		IWidgetLocator[] paneContents    = findInContentPane(matcher);
		IWidgetLocator[] paletteContents = findInPalette(matcher);
		return merge(paneContents, paletteContents);
	}

	private IWidgetLocator[] merge(IWidgetLocator[] paneContents, IWidgetLocator[] paletteContents) {
		
		Set merged = new HashSet();
		for (int i = 0; i < paneContents.length; i++) {
			merged.add(paneContents[i]);
		}
		for (int i = 0; i < paletteContents.length; i++) {
			merged.add(paletteContents[i]);
		}
		
		return (IWidgetLocator[]) merged.toArray(noMatches());
	}

	private IWidgetLocator[] findInContentPane(IFigureMatcher matcher) {
		IFigure contentPane = getEditor().getContentPane();
		if (contentPane == null)
			return noMatches();	
		return findAll(matcher, contentPane);
	}
	private IWidgetLocator[] findInPalette(IFigureMatcher matcher) {
		
		PaletteViewer viewer = getEditor().getPaletteViewer();
		if (viewer == null)
			return noMatches();
		
		IFigure contentPane = GEFFinder.getDefault().getFigure(viewer.getContents());
		if (contentPane == null)
			return noMatches();
		
		return findAll(matcher, contentPane);
	}	
	
	private IWidgetLocator[] findAll(IFigureMatcher matcher, IFigure contentPane) {
		return Draw2DFinder.getDefault().findAllFigureLocators(contentPane, matcher);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IEditPartSearchScope#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.gef.IGEFEditPartMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IGEFEditPartMatcher matcher) {
		return GEFFinder.getDefault().findAllEditPartReferences(getEditor().getViewer(), matcher);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.finder.scope.IWidgetSearchScope#findAll(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetMatcher)
	 */
	public IWidgetLocator[] findAll(IUIContext ui, IWidgetMatcher matcher) {
		GraphicalViewer viewer = getEditor().getViewer();
		if (viewer == null)
			return noMatches();
		Control control = viewer.getControl();
		if (control == null)
			return noMatches();
		return new BasicWidgetFinder().findAllLocators(control, matcher);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.scope.AbstractScope#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == IWidgetLocator.class)
			return getEditor().getEditor();
		return super.getAdapter(adapter);
	}

	
}
