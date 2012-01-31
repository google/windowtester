package com.windowtester.test.eclipse;

import org.eclipse.swt.SWT;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;


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
public class CompoundKeystrokeSmokeTest extends BaseTest {


	public void testTriggerOpenResourceDialog() throws Exception {
		IUIContext ui = getUI();
//		ui.keyClick(WT.CTRL | WT.SHIFT, 'r');
		ui.keyClick(SWT.MOD1 | SWT.SHIFT, 'r');
		ui.wait(new ShellShowingCondition("Open Resource"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Open Resource"));
	}

	public void testTriggerOpenNewWizard() throws Exception {
		IUIContext ui = getUI();
//		ui.keyClick(WT.CTRL, 'n');
		ui.keyClick(SWT.MOD1, 'n');
		ui.wait(new ShellShowingCondition("New"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("New"));
	}
	
	@Override
	protected void tearDown() throws Exception {
		//to address: https://fogbugz.instantiations.com/fogbugz/default.asp?45273
		//where a cascade of errors was caused by an inactive shell
		ensureWorkbenchIsInFront();
		super.tearDown();
	}
}
