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

import java.awt.Point;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.locator.JTableItemLocator;

public class SimpleTableTest extends UITestCaseSwing {

	/**
	 * Create an Instance
	 */
	public SimpleTableTest() {
		super(swing.samples.SimpleTable.class);
	}

	/**
	 * Main test method.
	 */
	public void testMain() throws Exception {
		IUIContext ui = getUI();
		ui.click(new JTableItemLocator(new Point(0,1)));
		ui.click(new JTableItemLocator(new Point(2,3)));
	}
}