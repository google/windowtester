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
package com.windowtester.test.cases.linux;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.eclipse.WorkbenchLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

/**
 * @author Jaime Wren
 */
public class Case42532Test extends UITestCaseSWT {

	/* @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		IUIContext ui = getUI();
		ui.ensureThat(new WorkbenchLocator().hasFocus());
		ui.ensureThat(ViewLocator.forName("Welcome").isClosed());
	}

	/**
	 * Main test method.
	 * <p>
	 * Recorded myself.
	 */
	public void SAME_AS_TEST_BELOW_testCase42532_1() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Help/About Eclipse Platform"));
		ui.wait(new ShellShowingCondition("About Eclipse Platform"));
		ui.click(new SWTWidgetLocator(Composite.class, new SWTWidgetLocator(
				Composite.class, 1, new SWTWidgetLocator(Composite.class,
						new SWTWidgetLocator(Shell.class,
								"About Eclipse Platform")))));
		ui.click(new ButtonLocator("&Installation Details"));
		ui.wait(new ShellShowingCondition(
				"Eclipse Platform Installation Details"));
		// breaking here for customer!
		ui.click(new ButtonLocator("&Close"));
		ui.wait(new ShellDisposedCondition(
				"Eclipse Platform Installation Details"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("About Eclipse Platform"));
	}
	
	/**
	 * Emailed from customer.
	 */
	public void testCase42532_2() throws Exception {
		  IUIContext ui = getUI();
		  ui.click(new MenuItemLocator("Help/About Eclipse Platform"));
		  ui.wait(new ShellShowingCondition("About Eclipse Platform"));
		  ui.click(new SWTWidgetLocator(Composite.class, new SWTWidgetLocator(
		    Composite.class, 1, new SWTWidgetLocator(Composite.class,
		    new SWTWidgetLocator(Shell.class,
		    "About Eclipse Platform")))));
		  ui.click(new ButtonLocator("&Installation Details"));
		  ui.wait(new ShellShowingCondition(
		    "Eclipse Platform Installation Details"));
		  ui.click(new ButtonLocator("&Close"));
		  ui.wait(new ShellDisposedCondition(
		    "Eclipse Platform Installation Details"));
		  ui.click(new ButtonLocator("OK"));
		  ui.wait(new ShellDisposedCondition("About Eclipse Platform"));
		}
	
}