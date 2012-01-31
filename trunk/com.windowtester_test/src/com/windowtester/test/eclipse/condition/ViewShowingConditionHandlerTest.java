package com.windowtester.test.eclipse.condition;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import org.eclipse.ui.views.IViewDescriptor;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.internal.condition.eclipse.ViewShowingConditionHandler;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;

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
@SuppressWarnings("restriction")
public class ViewShowingConditionHandlerTest extends BaseTest {

	private static final int REPEATS = 5;

	/* (non-Javadoc)
	 * @see com.windowtester.test.eclipse.BaseTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getUI().ensureThat(view("Search").isClosed());
	}
	
	public void testShowing() throws Exception {
		IUIContext ui = getUI();
		for (int i=0; i < REPEATS; ++i) {
			ui.ensureThat(view("Search").isShowing());
			ui.assertThat(view("Search").isShowing());
			ui.assertThat(view("Search").isVisible());
			ui.ensureThat(view("Search").isClosed());
		}
	}
	
	public void testFindViewByName() throws Exception {
		IViewDescriptor viewDescriptor = ViewLocator.forId(
				"org.eclipse.jdt.ui.PackageExplorer").getDescriptor();
		assertNotNull(viewDescriptor);
	}

	public void testFindViewById() throws Exception {
		IViewDescriptor viewDescriptor = ViewLocator.forName("Package Explorer").getDescriptor();
		assertNotNull(viewDescriptor);
	}
	
	
}
