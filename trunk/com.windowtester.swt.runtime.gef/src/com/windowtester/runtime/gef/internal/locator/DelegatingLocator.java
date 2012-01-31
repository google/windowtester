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

import com.windowtester.internal.runtime.locator.IAdaptableWidgetLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.locator.IFigureLocator;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * A locator that delegates all responsibilities to an {@link IAdaptableWidgetLocator}
 * delegate.
 * <p>
 * <code>DelegatingLocator</code>s are useful when you want to encapsulate internal details
 * and not have them leak into the API.
 * 
 */
public class DelegatingLocator implements IAdaptableWidgetLocator, IFigureLocator, Serializable {

	private static final long serialVersionUID = -8636480430116489303L;

	//does the heavy lifting
	private final IAdaptableWidgetLocator _delegate;
	

	///////////////////////////////////////////////////////////////////////////////
	//
	// Instance creation
	//
	///////////////////////////////////////////////////////////////////////////////

	protected DelegatingLocator(IAdaptableWidgetLocator delegate) {
		_delegate = delegate;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Internal state
	//
	///////////////////////////////////////////////////////////////////////////////
	
	private IAdaptableWidgetLocator getDelegate() {
		return _delegate;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// Public API
	//
	///////////////////////////////////////////////////////////////////////////////
	
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


}
