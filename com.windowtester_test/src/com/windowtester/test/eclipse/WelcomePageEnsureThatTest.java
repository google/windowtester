package com.windowtester.test.eclipse;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

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
public class WelcomePageEnsureThatTest extends UITestCaseSWT {

	private static final int REPEATS = 5;


	public void testAlreadyClosed() throws Exception {	
		IUIContext ui = getUI();		
		ui.ensureThat(view("Welcome").isClosed());
		ui.assertThat(view("Welcome").isClosed());
		for (int i = 0; i < REPEATS; i++) {
			ui.ensureThat(view("Welcome").isClosed());
			ui.assertThat(view("Welcome").isClosed());
		}
	}

	public void testOpened() throws Exception {
		for (int i = 0; i < REPEATS; i++) {
			openWelcome();
			getUI().ensureThat(view("Welcome").isClosed());
		}
	}
	
	
	private void openWelcome() throws WidgetSearchException {
		CoreWorkbenchActions.showViewNamed("Welcome");
		getUI().wait(ViewLocator.forName("Welcome").isVisible());
	}
	
	
	
	
}
