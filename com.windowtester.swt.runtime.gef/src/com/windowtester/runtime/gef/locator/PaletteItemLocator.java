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
package com.windowtester.runtime.gef.locator;

import java.io.Serializable;

import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.internal.locator.PaletteItemLocatorDelegate;
import com.windowtester.runtime.gef.internal.selectors.PaletteItemRevealer;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;

/**
 * Locates palette items.
 * <p>
 * By default search for palette items is scoped by the current active editor.
 * If another editor is desired, an editor can be specified.
 */
public class PaletteItemLocator implements IWidgetLocator, IAdaptable, Serializable {

	private static final long serialVersionUID = 4010071949066347423L;

	private final PaletteItemLocatorDelegate _delegate;
	
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Policies (note: provisional and to be moved)
	//
	////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * A provisional accessor to control whether locators should
	 * try and reveal the palette before selecting items in it.  This
	 * is primarily for testing purposes and is not strictly API. 
	 * <p>
	 * <strong>PROVISIONAL</strong>. This method has been added as
	 * part of a work in progress. There is no guarantee that this API will remain the same. 
	 * Please do not use this API for more than experimental purpose without consulting with 
	 * the WindowTester team.
	 * 
	 */
	public static void usePaletteRevealer(boolean doPaletteReveal) {
		if (!doPaletteReveal)
			PaletteItemRevealer.disableRevealing();
		else
			PaletteItemRevealer.enableRevealing();
	}	
		
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Instance creation
	//
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Create a locator that locates the palette item identified by a path String in the 
	 * currently active editor's palette.
	 * 
	 * @param itemPath the path to the item (e.g., "Shapes/Ellipse")
	 */
	public PaletteItemLocator(String itemPath) {
		_delegate = new PaletteItemLocatorDelegate(itemPath);
	}

	/**
	 * Create a locator that locates the palette item identified by a path String 
	 * in the given editor's palette.
	 * @param itemPath the path to the item (e.g., "Shapes/Ellipse")
	 * @param editor an editor to scope the search
	 */
	public PaletteItemLocator(String itemPath, IEditorLocator editor) {
		_delegate = new PaletteItemLocatorDelegate(itemPath, editor);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//
	// Internal accessors
	//
	////////////////////////////////////////////////////////////////////////////////
	
	private PaletteItemLocatorDelegate getDelegate() {
		return _delegate;
	}

	////////////////////////////////////////////////////////////////////////////////
	//
	// API
	//
	////////////////////////////////////////////////////////////////////////////////
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return getDelegate().findAll(ui);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return getDelegate().matches(widget);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		return getDelegate().getAdapter(adapter);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getDelegate().toString();
	}
	
}
