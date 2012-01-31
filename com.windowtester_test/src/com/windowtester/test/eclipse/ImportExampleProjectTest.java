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
package com.windowtester.test.eclipse;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;

/**
 * Import the various example projects such as ContactManagerSwing using WindowTester's import example wizard
 */
public class ImportExampleProjectTest extends BaseTest
{
	public void testImportExampleSwingProject() throws Exception {
		testImportExampleProject("Contact Manager Swing Example Project");
	}

	public void testImportExampleRcpProject() throws Exception {
		testImportExampleProject("Contact Manager RCP Example Project");
	}

	public void testImportExampleSWTProject() throws Exception {
		testImportExampleProject("AddressBook SWT Example Project");
	}

	private void testImportExampleProject(String exampleTreeItemText) throws WidgetSearchException,
		WaitTimedOutException
	{
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Other..."));
		ui.wait(new ShellShowingCondition("New"));
		ui.click(new TreeItemLocator("WindowTester/" + exampleTreeItemText));
		ui.click(new ButtonLocator("Next >"));
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Project(s)"));
	}
}