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
package com.windowtester.runtime.locator;

import java.util.HashMap;
import java.util.Map;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.internal.runtime.locator.IUISelector2;
import com.windowtester.internal.runtime.system.WidgetSystem;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.IUIContext;

/**
 * A widget locator that directly references a widget.  (Used to adapt
 * widgets to widget locators.)
 */
public class WidgetReference<T>
	implements IWidgetReference, IAdaptable
{
	/**
	 * The referenced widget
	 */
	private final T widget;
	
	/**
	 * Adapters for the receiver indexed by class
	 * or <code>null</code> if not initialized by {@link #addAdapter(Class, Object)}.
	 */
	private Map<Class<?>, Object> adapters;
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Construction
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Create a widget reference locator for this widget.
	 * @param widget the widget
	 * @return an <code>IWidgetLocator</code> instance that 
	 * 			wraps this widget
	 */
	public static <T> WidgetReference<T> create(T widget) {
		return new WidgetReference<T>(widget);
	}

	/**
	 * Create a widget reference locator for this widget.
	 * @param widget the widget
	 * @param the selector for use in selecting the widget
	 * @return an <code>IWidgetLocator</code> instance that 
	 * 			wraps this widget
	 */
	public static <T> WidgetReference<T> create(T widget, final IUISelector selector) {
		WidgetReference<T> result = create(widget);
		result.addAdapter(IUISelector.class, selector);
		if (selector instanceof IUISelector2)
			result.addAdapter(IUISelector2.class, selector);
		return result;
	}

	/**
	 * Create an instance.
	 */
	/* default */
	public WidgetReference(T widget) {
		this.widget = widget;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Public accessors for IWidgetReference and IAdaptable
	//
	////////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return new IWidgetLocator[]{
			this
		};
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return this.widget == widget;
	}

	/**
	 * Get the referenced widget.
	 * @return the widget
	 */
	public T getWidget() {
		return widget;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapters != null) {
			Object result = adapters.get(adapter);
			if (result != null)
				return result;
		}
		if (IUISelector.class == adapter)
			return WidgetSystem.getDefaultSelector(this);
		//next ask the IUISelector (if there is one)
		Object selector = adapters.get(IUISelector.class);
		if (selector instanceof IAdaptable)
			return ((IAdaptable)selector).getAdapter(adapter);
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "WidgetReference(" + widget + ")";
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Internal methods
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Add an adapter to be returned by {@link #getAdapter(Class)}
	 * @param adapter the class (key)
	 * @param value the adapter object to be returned
	 */
	public void addAdapter(Class<?> adapter, Object value) {
		if (adapters == null)
			adapters = new HashMap<Class<?>, Object>();
		adapters.put(adapter, value);
	}
}
