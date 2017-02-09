/*******************************************************************************
 *
 *   Copyright (c) 2012 Google, Inc.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *   
 *   Contributors:
 *   Google, Inc. - initial API and implementation
 *******************************************************************************/
 
package com.windowtester.example.contactmanager.rcp.test;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;

public class NewEntryTest extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testNewEntry() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New Contact..."));
		ui.wait(new ShellShowingCondition(""));
		ui.enterText("James");
		ui.keyClick(WT.TAB);
		ui.enterText("Smith");
		ui.click(new LabeledTextLocator("Street"));
		ui.enterText("645 NW 1st Ave");
		ui.click(new LabeledTextLocator("City"));
		ui.enterText("New York");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition(""));
		ui.click(2, new TableItemLocator("Smith,James", new ViewLocator(
				"com.windowtester.example.contactmanager.rcp.view")));
		ui.assertThat(new EditorLocator("Smith,James").isVisible());
	}

}