package com.windowtester.test.runtime;

import com.windowtester.internal.runtime.locator.LocatorIterator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

import junit.framework.TestCase;

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
public class LocatorIteratorTest extends TestCase {

	
	public void testSingleHasNext() {
		LocatorIterator locators = LocatorIterator.forLocator(new ButtonLocator("OK"));
		assertTrue(locators.hasNext());
		locators.next();
		assertFalse(locators.hasNext());
	}
	
	public void testNestedHasNext() {
		LocatorIterator locators = LocatorIterator.forLocator(new ButtonLocator("OK", new ViewLocator("foo")));
		assertTrue(locators.hasNext());
		locators.next();
		assertTrue(locators.hasNext());
		locators.next();
		assertFalse(locators.hasNext());
	}
	
	public void testXYHasNext() {
		LocatorIterator locators = LocatorIterator.forLocator(new XYLocator(new ButtonLocator("foo"), 4, 4));
		assertTrue(locators.hasNext());
		locators.next();
		assertTrue(locators.hasNext());
		locators.next();
		assertFalse(locators.hasNext());
	}
	
	
	
}
