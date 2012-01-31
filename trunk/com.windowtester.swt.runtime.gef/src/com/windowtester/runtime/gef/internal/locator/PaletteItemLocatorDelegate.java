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

import java.io.Serializable;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.palette.PaletteViewer;

import com.windowtester.internal.runtime.finder.IIdentifierHintProvider;
import com.windowtester.internal.runtime.locator.IUISelector2;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.internal.finder.IFigureFinder;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.internal.finder.MultiplePartsFoundException;
import com.windowtester.runtime.gef.internal.finder.PartNotFoundException;
import com.windowtester.runtime.gef.internal.finder.RevealingFinder;
import com.windowtester.runtime.gef.internal.helpers.PalettePartFinder;
import com.windowtester.runtime.gef.internal.identifier.PaletteItemIdentifier;
import com.windowtester.runtime.gef.internal.matchers.PaletteItemPartMatcher;
import com.windowtester.runtime.gef.internal.selectors.IEditPartProvider;
import com.windowtester.runtime.gef.internal.selectors.IPaletteViewerProvider;
import com.windowtester.runtime.gef.internal.selectors.IRevealer;
import com.windowtester.runtime.gef.internal.selectors.PaletteItemSelector;
import com.windowtester.runtime.locator.IItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.finder.RetrySupport;
import com.windowtester.runtime.swt.internal.finder.RetrySupport.Clickable;
import com.windowtester.runtime.swt.locator.eclipse.ActiveEditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;

/**
 * A locator delegate for palette item figures.
 */
public class PaletteItemLocatorDelegate extends FigureLocatorDelegate implements IItemLocator, IIdentifierHintProvider, Serializable {

	
	private static final long serialVersionUID = 8895478461592424401L;

	/**
	 * A class used as a back-pointer to bridge services provided by the locator to the selector.
	 *
	 */
	private class SelectorBridge implements IEditPartProvider, IPaletteViewerProvider {
		public EditPart getPart() throws MultiplePartsFoundException, PartNotFoundException {
			return getPartFinder().getEditPart();
		}
		public PaletteViewer getViewer() {
			return getPartFinder().getPaletteViewer();
		}
	}
	
	//initialized in constructor
	private final PalettePartFinder partFinder;
	private final String itemPath;
	
	
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Instance creation
	//
	////////////////////////////////////////////////////////////////////////////////
	
	public PaletteItemLocatorDelegate(String itemPath) {
		this(itemPath, unspecifiedEditor());
	}

	public PaletteItemLocatorDelegate(String itemPath, IEditorLocator editor) {
		super(createPartMatcher(itemPath));
		setScope(ScopeFactory.editor(editor));
		this.partFinder = new PalettePartFinder(itemPath, editor);
		this.itemPath = itemPath;
	}
	
	/**
	 * Create a locator that matches any editor.
	 */
	private static IEditorLocator unspecifiedEditor() {
		return new ActiveEditorLocator();
	}
	
	/**
	 * Build a part matcher out of this path.
	 */
	private static IFigureMatcher createPartMatcher(String path) {
		return new PaletteItemPartMatcher(path);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////////

	protected final PalettePartFinder getPartFinder() {
		return partFinder;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IItemLocator#getPath()
	 */
	public String getPath() {
		return itemPath;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Specialized locator behavior
	//
	////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.locator.FigureLocatorDelegate#createSelector()
	 */
	protected IUISelector2 createSelector() {
		SelectorBridge bridge = new SelectorBridge();
		return new PaletteItemSelector(bridge, bridge, getPath());
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.FigureLocator2#createFinder()
	 */
	protected IFigureFinder createFinder() {
		//take the standard finder and add revealing
		return new RevealingFinder(super.createFinder(), new IRevealer() {
			public void reveal(IUIContext ui) throws WidgetSearchException {
				((PaletteItemSelector)getSelector()).reveal(ui);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.FigureLocator#getFigureIdentifier()
	 */
	protected IFigureIdentifier getFigureIdentifier() {
		return new PaletteItemIdentifier();
	}
		
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.locator.FigureLocatorDelegate#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == IItemLocator.class) 
			return this;
		if (adapter == IIdentifierHintProvider.class)
			return this;
		return super.getAdapter(adapter);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(final IUIContext ui, IWidgetReference widget /* N.B. recalculated */ , final IClickDescription click) throws WidgetSearchException {
		
		return RetrySupport.performClickWithRetries(new Clickable() {
			public IWidgetLocator click() throws WidgetSearchException {
				//NOTE: we are do a find retry since the reference might change
				//(As in when a new editor is activated)
				IWidgetReference toClick = (IWidgetReference) ui.find(PaletteItemLocatorDelegate.this);
				return getSelector().click(ui, toClick, click);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(final IUIContext ui, IWidgetReference widget /* N.B. recalculated */, final IClickDescription click, final String menuItemPath) throws WidgetSearchException {
		
		return RetrySupport.performClickWithRetries(new Clickable() {
			public IWidgetLocator click() throws WidgetSearchException {
				//NOTE: we are do a find retry since the reference might change
				//(As in when a new editor is activated)
				IWidgetReference toClick = (IWidgetReference) ui.find(PaletteItemLocatorDelegate.this);
				return getSelector().contextClick(ui, toClick, click, menuItemPath);
			}
		});
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Debugging
	//
	////////////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PaletteItemLocator(\"").append(getPartFinder().getItemPath()).append("\"");
		IEditorLocator editor = getPartFinder().getEditor();
		if (editor != null)
			sb.append(", ").append(editor);
		sb.append(")");
		return sb.toString();
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Identification hints
	//
	////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.finder.IIdentifierHintProvider#requiresXY()
	 */
	public boolean requiresXY() {
		return false;
	}
	
}

