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
 
package com.windowtester.example.contactmanager.swing.test;

import com.windowtester.runtime.swing.locator.JMenuItemLocator;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import java.awt.event.KeyEvent;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swing.locator.LabeledTextLocator;
import com.windowtester.runtime.swing.locator.JButtonLocator;
import com.windowtester.runtime.swing.condition.WindowDisposedCondition;

public class NewContactTest extends UITestCaseSwing {

	/**
	 * Create an Instance
	 */
	public NewContactTest() {
		super(com.windowtester.example.contactmanager.swing.ContactManagerSwing.class);
	}

	/**
	 * Main test method.
	 */
	public void testNewContact() throws Exception {
		IUIContext ui = getUI();
		ui.click(new JMenuItemLocator("File/New Contact"));
		ui.wait(new WindowShowingCondition("New Contact"));
		ui.enterText("John");
		ui.keyClick(KeyEvent.VK_TAB);
		ui.enterText("Doe");
		ui.click(new LabeledTextLocator("Street: "));
		ui.enterText("789 Washinton Ave");
		ui.click(new LabeledTextLocator("City: "));
		ui.click(new JButtonLocator("Finish"));
		ui.wait(new WindowDisposedCondition("dialog0"));
		//ui.click(new JMenuItemLocator("File/Exit"));
	}

}