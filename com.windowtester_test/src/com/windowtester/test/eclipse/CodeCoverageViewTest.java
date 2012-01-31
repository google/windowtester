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

import org.eclipse.swt.widgets.Label;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

public class CodeCoverageViewTest extends BaseTest
{

	/**
	 * Main test method.
	 */
	public void testCodeCoverageView() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Other..."));
		ui.wait(new ShellShowingCondition("Show View"));
		ui.click(new TreeItemLocator("Code Coverage/Code Coverage"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Show View"));
		
		boolean evaluation = ui.findAll(new SWTWidgetLocator(Label.class, "*Evaluation*", new ViewLocator(
			"com.instantiations.assist.eclipse.coverage.codeCoverageView"))).length > 0;
		boolean unactivated = ui.findAll(new SWTWidgetLocator(Label.class, "*Activation Required*", new ViewLocator(
			"com.instantiations.assist.eclipse.coverage.codeCoverageView"))).length > 0;
		
		ui.ensureThat(new CTabItemLocator("Code Coverage").isClosed());
		
		
		assertFalse(evaluation);
		assertFalse(unactivated);
	}

}