package com.windowtester.test.eclipse.condition;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IsSelected;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.util.TypingLinuxHelper;

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
public class MenuItemSelectionTest extends BaseTest {

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.common.UITestCaseCommon#oneTimeSetup()
	 */
	protected void oneTimeSetup() throws Exception {
		IUIContext ui = getUI();
		createJavaProject(ui, getClass().getName());
		super.setUp();
	}
	
	public void testSuccess() throws WidgetSearchException {
		getUI().assertThat(new MenuItemLocator("Project/Build Automatically").isSelected());
	}
	public void testFailure() throws WidgetSearchException {
		try {
			getUI().assertThat(new MenuItemLocator("Project/Build Automatically").isSelected(false));
			fail();
		} catch(WaitTimedOutException e) {
			//pass
		}
	}

	
	
	private void createJavaProject(IUIContext ui, String projectName) throws WidgetSearchException {
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("Next >"));
		ui.assertThat(new ButtonLocator("Finish").isEnabled(false));
		ui.enterText(projectName);
		ui.assertThat(new ButtonLocator("Finish").isEnabled(true));
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		ui.wait(new JobsCompleteCondition());
		ui.wait(new SWTIdleCondition());
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}
	
	
	
}
