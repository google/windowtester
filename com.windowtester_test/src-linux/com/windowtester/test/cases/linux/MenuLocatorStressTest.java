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
package com.windowtester.test.cases.linux;

import static com.windowtester.runtime.swt.locator.SWTLocators.menuItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.test.locator.swt.AbstractLocatorTest;
import com.windowtester.test.locator.swt.shells.MenuTestShell;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MenuLocatorStressTest {

	private static final int REPEATS = 20;
	
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Menu Locator stress test");
		//$JUnit-BEGIN$

		for (int i=0; i < REPEATS; ++i)		
			suite.addTestSuite(MenuTest.class);
		
		//$JUnit-END$
		return suite;
	}
	
	public static class MenuTest extends AbstractLocatorTest {
		
		private static final String RUN_PATTERN = "Run...";

		private static final String RUN_MENU = "Run";
		
		MenuTestShell window;
		
		public void testRunPatternMatching() throws Exception {
			IUIContext ui = getUI();
			ui.click(menuItem(RUN_MENU + "/" + RUN_PATTERN));
		} 
		
		@Override
		public void uiSetup() {
			window = new MenuTestShell();
			window.open();
		} 
		
		@Override
		public void uiTearDown() {
			window.getShell().dispose();
		}
	}
	

}
