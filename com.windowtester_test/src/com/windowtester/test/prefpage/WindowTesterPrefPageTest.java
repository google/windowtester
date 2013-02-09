package com.windowtester.test.prefpage;

import abbot.Platform;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
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
public class WindowTesterPrefPageTest extends BaseTest
{
	
	private static final String PREFERENCES_MENU_ITEM_PATH = "Window/&Preferences(...)?"; //3.4M7+-safe

	
	/* (non-Javadoc)
	 * @see com.windowtester.test.eclipse.BaseTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		openPreferences(getUI());
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		dismissPreferences(getUI());
		super.tearDown();
	}
	
	public void testWindowTesterPrefPages() throws Exception {
		IUIContext ui = getUI();
		checkCodegenPage(ui);
		//License page does not exist anymore
//		checkLicensePage(ui);
		checkPlaybackPage(ui);

	}


	private void dismissPreferences(IUIContext ui)
			throws WidgetSearchException, WaitTimedOutException {
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}


	private void openPreferences(IUIContext ui)
			throws WidgetSearchException, WaitTimedOutException {
		if (Platform.isOSX())
			ui.keyClick(WT.COMMAND, ',');
		else
			ui.click(new MenuItemLocator(PREFERENCES_MENU_ITEM_PATH));
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new TreeItemLocator("WindowTester"));
	}

	//License page does not exist anymore
//	private void checkLicensePage(IUIContext ui) throws WidgetSearchException {
//		ui.click(new TreeItemLocator("WindowTester/License"));
//	}


	private void checkPlaybackPage(IUIContext ui) throws WidgetSearchException {
		ui.click(new TreeItemLocator("WindowTester/Playback"));
		ui.click(new XYLocator(new LabeledTextLocator("&Highlight duration (in milliseconds)"), 34, 5));
		ui.assertThat(new LabeledTextLocator("&Highlight duration (in milliseconds)").isVisible());
	}


	private void checkCodegenPage(IUIContext ui) throws WidgetSearchException {
		ui.click(new TreeItemLocator("WindowTester/Code Generation"));
		//TODO: update with static codegen prefs.
		//ui.click(new ButtonLocator("API Version 1 (deprecated)"));
	}
}