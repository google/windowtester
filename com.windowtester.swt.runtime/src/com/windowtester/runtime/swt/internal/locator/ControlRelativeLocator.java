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
package com.windowtester.runtime.swt.internal.locator;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.IncompatibleTypeException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * An SWT Widget locator that locates a widget (represented as an object not
 * a virtual object as in {@link VirtualItemLocator}s) relative to a Control.
 */
public abstract class ControlRelativeLocator extends SWTWidgetLocator {

	private static final long serialVersionUID = -171899412731125649L;


	public ControlRelativeLocator(Class<?> cls) {
		super(cls);
	}
	
	//child
	public ControlRelativeLocator(Class<?> cls, IWidgetLocator parent) {
		this(cls, UNASSIGNED, parent);
	}

	//indexed child
	public ControlRelativeLocator(Class<?> cls, int index, IWidgetLocator parent) {
		super(cls);
		setIndex(index);
		setParentInfo(adaptParentLocator(parent));
	}
	
	/**
	 * Get the parent control.  It is guaranteed to be of the same type as
	 * that returned by <code>getControlType()<code>. 
	 * @throws WidgetSearchException 
	 */
	protected Widget getControl(IUIContext ui) throws WidgetSearchException {
		IWidgetReference locator = (IWidgetReference)ui.find(getParentInfo());
		Widget w =  (Widget) locator.getWidget();
		assertTypeEquals(w, getControlType());
		return w;
	}
	
	/**
	 * Verify that the type of the given widget is as expected, else throw an exception.
	 */
	protected void assertTypeEquals(Widget w, Class<?> cls) {
		if (!cls.isAssignableFrom(w.getClass()))
			throw new IncompatibleTypeException();
	}
	
	
	/**
	 * Adapt/embelish parent locator.
	 */
	private SWTWidgetLocator adaptParentLocator(IWidgetLocator parent) {
		SWTWidgetLocator locator = SWTWidgetLocator.adapt(parent);
		//add type information to the view locator
		if (locator instanceof ViewLocator)
			locator = new SWTWidgetLocator(getControlType(), locator);
		return locator;
	}

	/**
	 * Get the type of the containing control.
	 */
	protected abstract Class<?> getControlType();
	
}
