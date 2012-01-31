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
package com.windowtester.test.license;

import abbot.Platform;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.TestMonitor;
import com.windowtester.test.eclipse.BaseTest;

public class WindowTesterEvalRegTest extends BaseTest
{
	
	private static final String PREFERENCES_MENU_ITEM_PATH = "Window/&Preferences(...)?"; //3.4M7+-safe

	
	public void testWindowTesterEvalRegistration() throws Exception {
		testProductEvalWizard("WindowTester Pro");
	}

	protected void testProductEvalWizard(String buttonLabel) throws WidgetSearchException,
		WaitTimedOutException
	{
		IUIContext ui = getUI();
		if (Platform.isOSX())
			ui.keyClick(WT.COMMAND, ',');
		else
			ui.click(new MenuItemLocator(PREFERENCES_MENU_ITEM_PATH));
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new TreeItemLocator("WindowTester/License"));
		ui.click(new ButtonLocator("Registration and Activation"));
		ui.wait(new ShellShowingCondition("Product Registration and Activation"));

		// Locate the desired product evaluation radio button
		boolean found;
		try {
			ui.find(new ButtonLocator(buttonLabel));
			found = true;
		}
		catch (WidgetSearchException e) {
			String testcaseID = TestMonitor.getInstance().getCurrentTestCaseID();
			ScreenCapture.createScreenCapture(testcaseID + "_" + buttonLabel);
			found = false;
		}

		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Product Registration and Activation"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));

		assertTrue("Failed to find radio button labeled \"" + buttonLabel + "\" in Activation Wizard", found);
	}
}