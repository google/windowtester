package com.windowtester.test.eclipse;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.jface.WizardErrorMessageLocator;

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
public class NewSimpleProjectTest extends BaseTest {


	public void testNewMyProject() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("(Simple|General)/Project"));
		ui.click(new ButtonLocator("Next >"));
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), false));
		ui.enterText("MyProject");
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), true));
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Project"));
		ui.wait(new JobsCompleteCondition());
		ui.wait(new SWTIdleCondition());
	}

	public void testInvalidProjectNameInWizard() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("(Simple|General)/Project"));
		ui.click(new ButtonLocator("Next >"));
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), false));
		ui.assertThat(new HasTextCondition(new WizardErrorMessageLocator(), null));
		ui.enterText("/");
		ui.assertThat(new HasTextCondition(new WizardErrorMessageLocator(), ".*invalid.*"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("New Project"));
	}
}