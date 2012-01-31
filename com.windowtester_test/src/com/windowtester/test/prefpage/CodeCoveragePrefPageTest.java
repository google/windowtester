package com.windowtester.test.prefpage;

import abbot.Platform;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
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
public class CodeCoveragePrefPageTest extends BaseTest
{
	
	private static final String PREFERENCES_MENU_ITEM_PATH = "Window/&Preferences(...)?"; //3.4M7+-safe

	
	public void testCodeCoveragePrefPages() throws Exception {
		IUIContext ui = getUI();
		if (Platform.isOSX())
			ui.keyClick(WT.COMMAND, ',');
		else
			ui.click(new MenuItemLocator(PREFERENCES_MENU_ITEM_PATH));
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new TreeItemLocator("WindowTester/Code Coverage"));
		ui.click(new XYLocator(new LabeledTextLocator("Keep "), 9, 6));
		ui.click(new XYLocator(new LabeledTextLocator("Threshold:"), 25, 7));
		ui.click(new TreeItemLocator("WindowTester/Code Coverage/Data Locations"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}
}