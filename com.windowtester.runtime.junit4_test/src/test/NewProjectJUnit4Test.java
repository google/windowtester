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
package test;

import static com.windowtester.runtime.junit4.UIFactory.getUI;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.junit4.TestRunnerSWT;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;

/**
 * Sample UI PDE test done JUnit4-style.
 *
 * @author Phil Quitslund
 *
 */
@RunWith(TestRunnerSWT.class)
public class NewProjectJUnit4Test {

	@BeforeClass
	public static void closeWelcomeScreen() throws Exception {
		IUIContext ui = getUI();
		IWidgetLocator[] welcomeTab = ui.findAll(new CTabItemLocator("Welcome"));
		if (welcomeTab.length == 0)
			return;
		ui.contextClick(welcomeTab[0], "Close");
	}
	
	@Test
	public void verifyNewProjectCreation() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("(Simple|General)/Project"));
		ui.click(new ButtonLocator("Next >"));
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), false));
		ui.enterText(getTestProjectName());
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), true));
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Project"));
		ui.wait(new JobsCompleteCondition());
		ui.wait(new SWTIdleCondition());
	}
	

	//this isn't REALLY how you'd do it but, it shows off the feature...
	@Test(expected=WidgetSearchException.class)
	public void verifyButtonDoesNotExist() throws WidgetSearchException {
		getUI().find(new ButtonLocator("there is no such button"));
	}
	
	private String getTestProjectName() {
		return getClass().getSimpleName();
	}
	
}
