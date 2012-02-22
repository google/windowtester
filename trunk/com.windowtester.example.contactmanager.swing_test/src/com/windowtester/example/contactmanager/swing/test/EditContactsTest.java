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

import java.awt.event.KeyEvent;

import com.windowtester.runtime.swing.UITestCaseSwing;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.locator.JListLocator;
import com.windowtester.runtime.swing.locator.JMenuItemLocator;
import com.windowtester.runtime.swing.locator.JTabbedPaneLocator;
import com.windowtester.runtime.swing.locator.LabeledTextLocator;

public class EditContactsTest extends UITestCaseSwing {

	/**
	 * Create an Instance
	 */
	public EditContactsTest() {
		super(com.windowtester.example.contactmanager.swing.ContactManagerSwing.class);
	}

	/**
	 * 
	 */
	public void testEditContacts() throws Exception {
		IUIContext ui = getUI();
		ui.click(2, new JListLocator("James,Bond"));
		ui.click(2, new JListLocator("Perry,Mason"));
		ui.click(2, new JListLocator("Sam,Little"));
		ui.click(new LabeledTextLocator("Street: "));
		ui.enterText("657");
		ui.keyClick(KeyEvent.VK_TAB);
		ui.enterText("Los Angles");
		ui.click(new LabeledTextLocator("State: "));
		ui.enterText("CA");
		ui.click(new JTabbedPaneLocator("Perry,Mason"));
		ui.click(new LabeledTextLocator("Street: "));
		ui.enterText("675 Lincoln Ave");
	//	ui.click(new JMenuItemLocator("File/Exit"));
	}

}