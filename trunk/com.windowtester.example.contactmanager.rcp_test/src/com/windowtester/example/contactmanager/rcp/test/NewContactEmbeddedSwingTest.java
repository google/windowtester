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

import java.awt.event.KeyEvent;

import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

public class NewContactEmbeddedSwingTest extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testNewContactSwingDialog() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New Contact...(Embedded AWT Frame)"));
		ui.wait(new ShellShowingCondition(""));
		
		IUIContext uiSwing = (IUIContext) ui.getAdapter(UIContextSwing.class);
		uiSwing.enterText("Mary");
		uiSwing.keyClick(KeyEvent.VK_TAB);
		uiSwing.enterText("Higgins");
		uiSwing.click(new LabeledTextLocator("Street: "));
		uiSwing.enterText("No 1 Vermont St");
		uiSwing.click(new LabeledTextLocator("City: "));
		uiSwing.enterText("Vermont");
		
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition(""));
		ui.click(2, new TableItemLocator("Higgins,Mary", new ViewLocator(
		"com.windowtester.example.contactmanager.rcp.view")));
	}

}
