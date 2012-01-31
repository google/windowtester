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
package com.windowtester.internal.runtime.locator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.windowtester.runtime.IWidgetLocatorVisitor;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.XYLocator;

/**
 * Composite locator iterator helper.
 *
 */
public class LocatorIterator {

	
	private final List<ILocator> locators = new ArrayList<ILocator>();
	private Iterator<ILocator> iterator;
	
	public LocatorIterator(ILocator locator) {
		addToList(locator);
		iterator = locators.iterator();
	}

	private void addToList(ILocator locator) {
		if (locator == null)
			return;
		if (locator instanceof XYLocator) {
			locators.add(locator);
			addToList(((XYLocator)locator).locator());
		}
		else if (locator instanceof IdentifierAdapter) {
			addToList(((IdentifierAdapter)locator).getLocator());
		}
		else if (locator instanceof WidgetLocator) {
			((WidgetLocator)locator).accept(new IWidgetLocatorVisitor() {
				public void visit(WidgetLocator locator) {
					locators.add(locator);
				}
			});
		}
		else {
			/*
			 * Temporarily use relection to iterate.  Cautious, for now,
			 * about adding iteration to any API.
			 */
			locators.add(locator);
			addToList(getWrappedLocator(locator));
		}
	}

	private ILocator getWrappedLocator(ILocator locator) {
		try {
			Method method = locator.getClass().getMethod("getOwner", (Class<?>[])null);
			return (ILocator) method.invoke(locator, (Object[])null);
		} catch (Throwable e) {
			//yes, do ignore.
		}
		try {
			Method method = locator.getClass().getMethod("getLocator", (Class<?>[])null);
			return (ILocator) method.invoke(locator, (Object[])null);
		} catch (Throwable e) {
			//yes, do ignore.
		}
		return null;
	}

	public static LocatorIterator forLocator(ILocator locator) {
		return new LocatorIterator(locator);
	}

	
	public boolean hasNext() {
		return iterator.hasNext();
	}

	public ILocator next() {
		return (ILocator) iterator.next();
	}

	
	
	
	
}
