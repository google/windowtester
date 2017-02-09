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
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.swing.condition.WindowDisposedCondition;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JButtonLocator;
import com.windowtester.runtime.swing.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

public class NewContactSwingTest extends UITestCaseSWT {
	
	/**
	 * Main test method.
	 */
	public void testNewContactSwingDialog() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New Contact...(Swing Dialog)"));
		
		IUIContext uiSwing = (IUIContext) ui.getAdapter(UIContextSwing.class);
		uiSwing.wait(new WindowShowingCondition("New Contact"));
		ui.wait(TimeElapsedCondition.milliseconds(500));
		uiSwing.enterText("John");
		uiSwing.keyClick(KeyEvent.VK_TAB);
		uiSwing.enterText("Doe");
		uiSwing.click(new LabeledTextLocator("Street: "));
		uiSwing.enterText("789 Washington Ave");
		uiSwing.click(new LabeledTextLocator("City: "));
		uiSwing.enterText("New York");
		uiSwing.click(new JButtonLocator("Finish"));
		uiSwing.wait(new WindowDisposedCondition("New Contact"));
		ui.wait(TimeElapsedCondition.milliseconds(500));
		
		ui.click(2, new TableItemLocator("Doe,John", new ViewLocator(
		"com.windowtester.example.contactmanager.rcp.view")));
	}

}
