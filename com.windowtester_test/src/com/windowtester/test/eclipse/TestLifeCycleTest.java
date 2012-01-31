package com.windowtester.test.eclipse;

import org.eclipse.swt.widgets.MenuItem;


import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

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
public class TestLifeCycleTest extends UITestCaseSWT {

	
	public void testOpenDialogClosedOnFindFailure() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("&File/&Import..."));
		try {
			ui.click(new ButtonLocator("BOGUS!"));			
			fail("exception should have been thrown!");
		} catch (WidgetSearchException e) {
			//expected exception to be thrown
		}
		//dialog should be dismissed
		
		//failures here tell us that the dialog was not dismissed
		ui.click(new MenuItemLocator("&File/&Import..."));
		ui.click(new ButtonLocator("Cancel"));
	}
	
	
	public void testOpenMenuClosedOnFindFailure() throws WidgetSearchException {
		IUIContext ui = getUI();
		try {
			ui.click(new MenuItemLocator("&File/&Bogus"));
		} catch (WidgetSearchException e) {
			//expected exception to be thrown
		}
		//menu should be dismissed
		fail("this test needs to be fixed");
		
		
		//this isn't cutting it...
		
		//but if this can be found it wasn't!
		ui.find(new SWTWidgetLocator(MenuItem.class, "&Import..."));
	}
	
	
}
