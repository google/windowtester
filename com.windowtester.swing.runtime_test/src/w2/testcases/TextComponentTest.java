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
package w2.testcases;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JTextComponentLocator;

public class TextComponentTest extends UITestCaseSwing {

	/**
	 * Create an Instance
	 */
	public TextComponentTest() {
		super(swing.samples.TextComponentDemo.class);
	}

	/**
	 * Main test method.
	 */
	public void testMain() throws Exception {
	
		IUIContext ui = getUI();
		
		ui.wait(new WindowShowingCondition("Text Component Demo"));
		JTextComponentLocator textComponentLocator = new JTextComponentLocator(253,JTextPane.class);
		ui.assertThat(textComponentLocator.isEnabled());
//		ui.ensureThat(textComponentLocator.hasFocus());
		ui.click(textComponentLocator);
		ui.keyClick(InputEvent.CTRL_DOWN_MASK, 'b');
		ui.enterText("the ");
		ui.click(new JTextComponentLocator(89,JTextPane.class));
		ui.enterText("from ");
		ui.keyClick(InputEvent.SHIFT_DOWN_MASK | KeyEvent.VK_LEFT);
		ui.keyClick(InputEvent.SHIFT_DOWN_MASK | KeyEvent.VK_LEFT);
		ui.keyClick(InputEvent.SHIFT_DOWN_MASK | KeyEvent.VK_LEFT);
//		ui.keyClick(InputEvent.SHIFT_DOWN_MASK | KeyEvent.VK_LEFT);
//		ui.keyClick(InputEvent.SHIFT_DOWN_MASK | KeyEvent.VK_LEFT);
		ui.pause(1000);
	}

}