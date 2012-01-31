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
package com.windowtester.test.matcher;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;

public class AbstractMatcherTest extends UITestCaseSWT {

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
	
	
	
	protected void assertMatches(Widget w, ISWTWidgetMatcher matcher) throws WidgetSearchException {
		Widget widget = findWidget(matcher);
		assertEquals(w, widget);
	}



	protected Widget findWidget(ISWTWidgetMatcher matcher) throws WidgetSearchException {
		return findWidgetRef(matcher).getWidget();
	}



	protected ISWTWidgetReference<?> findWidgetRef(ISWTWidgetMatcher matcher) throws WidgetSearchException {
		ISWTWidgetReference<?>[] widgets = findWidgetRefs(matcher);
		if (widgets.length ==1)
			return widgets[0];
		if (widgets.length == 0)
			throw new WidgetNotFoundException();
		throw new MultipleWidgetsFoundException();
	}



	protected ISWTWidgetReference<?>[] findWidgetRefs(ISWTWidgetMatcher matcher) {
		return SWTWidgetFinder.forActiveShell().findAll(matcher);
	}
	
	
}
