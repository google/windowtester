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
package context2.testcases;

import com.windowtester.runtime.swing.locator.JButtonLocator;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.condition.WindowDisposedCondition;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;

public class DialogSampleTest extends UITestCaseSwing {

	/**
	 * Create an Instance
	 */
	public DialogSampleTest() {
		super(swing.samples.DialogSample.class);
	}

	/**
	 * Main test method.
	 */
	public void testDialogSample() throws Exception {
		IUIContext ui = getUI();
		ui.click(new JButtonLocator("Yes"));
		ui.wait(new WindowShowingCondition("Inane error"));
		ui.click(new JButtonLocator("OK"));
		ui.wait(new WindowDisposedCondition("Inane error"));
	}

}