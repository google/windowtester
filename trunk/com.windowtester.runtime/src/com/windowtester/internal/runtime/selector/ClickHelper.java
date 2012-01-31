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
package com.windowtester.internal.runtime.selector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.internal.runtime.system.WidgetSystem;
import com.windowtester.runtime.ClickDescription;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IMenuItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.XYLocator;

/**
 * A helper class that parses and performs click commands.
 */
public class ClickHelper implements IClickDriver {
	
	private final IUIContext _ui;
	
	//for click listeners
	private List _listeners;

	public ClickHelper(IUIContext ui) {
		_ui = ui;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.selector.IClickDriver#click(int, com.windowtester.runtime2.locator.ILocator, int)
	 */
	public IWidgetLocator click(int clickCount, ILocator locator, int buttonMask) throws WidgetSearchException {
		//extract widget reference
		IWidgetLocator wl = getWidgetLocator(locator); //might be wrapped in an XYLocator
//		if (wl == null)
//			return absoluteClick(..)
		
		IWidgetReference widget = null; //should be a null object
		
		/*
		 * Don't look for path based widgets...
		 */
		if (!(wl instanceof IItemLocator))
			widget = doFind(wl); //if no WL is found, it's an absolute click? 

		//create click description
		IClickDescription click = createClickDescription(clickCount, locator, buttonMask);
	
		
		IUISelector selector = getSelector(wl);
		IWidgetLocator clicked = doClick(widget, click, selector);
		informClick(click, clicked);
		return clicked;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.selector.IClickDriver#contextClick(com.windowtester.runtime2.locator.ILocator, com.windowtester.runtime2.locator.IMenuItemLocator)
	 */
	public IWidgetLocator contextClick(ILocator locator, IMenuItemLocator menuItem) throws WidgetSearchException {
		
		IWidgetLocator wl = getWidgetLocator(locator);
		IWidgetReference widget = null; //should be a null object
		
		/*
		 * Don't look for path based widgets...
		 */
		if (!(wl instanceof IItemLocator))
			widget = doFind(wl); //if no WL is found, it's an absolute click? 

		//create click description
		IClickDescription click = createClickDescription(1 /*clickCount*/, locator, WT.BUTTON3);
		
		IUISelector selector = getSelector(wl);
		IWidgetLocator clicked = doContextClick(menuItem, widget, click, selector);
		informContextClick(click, clicked);
		return clicked;
	}


	
	
	
	/**
	 * Get the selector associated with this locator.
	 */
	private IUISelector getSelector(ILocator locator) {
		if (locator instanceof IUISelector)
			return (IUISelector)locator;
		IUISelector selector = null;
		if (locator instanceof IAdaptable)
			selector = (IUISelector)((IAdaptable)locator).getAdapter(IUISelector.class);
		if (selector != null)
			return selector;
		if (locator instanceof IWidgetReference)
			return WidgetSystem.getDefaultSelector(((IWidgetReference)locator).getWidget());
		throw new IllegalStateException();
	}


	/**
	 * Get the WidgetLocator associated with this ILocator.
	 */
	public static IWidgetLocator getWidgetLocator(ILocator locator) {
		if (locator instanceof IWidgetLocator)
			return (IWidgetLocator)locator;
		if (locator instanceof XYLocator) {
			//notice that arbitrary nesting is suported here -- should it be?
			return getWidgetLocator(((XYLocator)locator).locator());
		}
		//SHOULD THROW EXCEPTION HERE?
		return null;
	}

	private IClickDescription createClickDescription(int clickCount, ILocator locator, int buttonMask) {
		// TODO properly handle nested XYLocators
		return ClickDescription.create(clickCount, locator, buttonMask);
	}

	private IUIContext getUIContext() {
		return _ui;
	}	
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.selector.IClickDriver#addClickListener(com.windowtester.internal.runtime.selector.IClickDriver.Listener)
	 */
	public void addClickListener(Listener listener) {
		getListeners().add(listener);
	}
	
	
	private void informClick(IClickDescription click, IWidgetLocator clicked) {
		for (Iterator iter = getListeners().iterator(); iter.hasNext();) {
			Listener listener = (Listener) iter.next();
			listener.clicked(click, clicked);
		}
	}
	

	private void informContextClick(IClickDescription click, IWidgetLocator clicked) {
		for (Iterator iter = getListeners().iterator(); iter.hasNext();) {
			Listener listener = (Listener) iter.next();
			listener.contextClicked(click, clicked);
		}
	}
	
	
	private List getListeners() {
		if (_listeners == null)
			_listeners = new ArrayList();
		return _listeners;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//
	// Wrappers that adapt legacy exceptions appropriately.
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
	private IWidgetLocator doClick(IWidgetReference widget, IClickDescription click, IUISelector selector) throws WidgetSearchException {
//		try {
			return selector.click(getUIContext(), widget, click);
// TODO [Dan] Not a single subclass of WidgetSearchException implements IAdaptable, so I commented this out
//		} catch (WidgetSearchException e) {
//			if (e instanceof IAdaptable) {
//				WidgetSearchException adapted = (WidgetSearchException) ((IAdaptable)e).getAdapter(WidgetSearchException.class);
//				if (adapted != null)
//					throw adapted;
//			}
//			throw e;
//		}
	}


	private IWidgetReference doFind(IWidgetLocator wl) throws WidgetSearchException {
//		try {
			return (IWidgetReference) getUIContext().find(wl);
// TODO [Dan] Not a single subclass of WidgetSearchException implements IAdaptable, so I commented this out
//		} catch (WidgetSearchException e) {
//			if (e instanceof IAdaptable) {
//				WidgetSearchException adapted = (WidgetSearchException) ((IAdaptable)e).getAdapter(WidgetSearchException.class);
//				if (adapted != null)
//					throw adapted;
//			}
//			throw e;
//		}
	}


	private IWidgetLocator doContextClick(IMenuItemLocator menuItem, IWidgetReference widget, IClickDescription click, IUISelector selector) throws WidgetSearchException {
//		try {
			return selector.contextClick(getUIContext(), widget, click, menuItem.getPath());
// TODO [Dan] Not a single subclass of WidgetSearchException implements IAdaptable, so I commented this out
//		} catch (WidgetSearchException e) {
//			if (e instanceof IAdaptable) {
//				WidgetSearchException adapted = (WidgetSearchException) ((IAdaptable)e).getAdapter(WidgetSearchException.class);
//				if (adapted != null)
//					throw adapted;
//			}
//			throw e;
//		}
	}

	
}
