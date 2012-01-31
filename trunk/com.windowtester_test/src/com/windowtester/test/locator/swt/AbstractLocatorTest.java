package com.windowtester.test.locator.swt;

import java.util.Arrays;
import java.util.Collection;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

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
public class AbstractLocatorTest extends UITestCaseSWT {

	
	
	/** Made final to avoid accidental overrides.
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected final void setUp() throws Exception {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				uiSetup();
			}			
		});
	}
	
	
	/** Made final to avoid accidental overrides.
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected final void tearDown() throws Exception {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				uiTearDown();
			}
		});
	}
	
	
	/**
	 * Set up the test fixture --- will be run on the UI thread.
	 */
	public void uiSetup() {
		//
	}
	
	
	/**
	 * Set up the test fixture --- will be run on the UI thread.
	 */
	public void uiTearDown() {
		//
	}
	
	
	public final void wait(ICondition condition) {
		getUI().wait(condition, 3000);
	}
	
	
	////////////////////////////////////////////////////////////////////////
	//
	// Assertion helpers
	//
	////////////////////////////////////////////////////////////////////////
	
	public void assertContainsExactly(IStructuredSelection selection, Object[] elems) {
		assertContainsExactly(selection.toList(), Arrays.asList(elems));
	}
	
	public void assertContainsExactly(Collection<?> host, Collection<?> elems) {
		assertTrue(host.containsAll(elems));
	}
	
	public void assertContainsExactly(Object[] hosts, Object[] elems) {
		assertContainsExactly(Arrays.asList(hosts), Arrays.asList(elems));
	}

	public void assertTextEquals(String expected, StyledText text) {
		String result = text.getText();
		assertEquals(expected, result);
	}
	
}
