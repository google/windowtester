package com.windowtester.test.eclipse.locator;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import org.eclipse.swt.custom.StyledText;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.condition.eclipse.ActiveEditorCondition;
import com.windowtester.runtime.swt.condition.eclipse.ConfirmPerspectiveSwitchShellHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.helpers.JavaProjectHelper;

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
@SuppressWarnings("restriction")
public class EditorLocatorSmokeTest extends BaseTest {

	private static final String PROJECT_NAME = "Smoke";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IShellMonitor sm = (IShellMonitor) getUI().getAdapter(
				IShellMonitor.class);
		sm.add(new ConfirmPerspectiveSwitchShellHandler(true));
	}
	
	/**
	 * Simple find.
	 */
	public void testFindAndClick() throws Exception {
		IUIContext ui = getUI();

		JavaProjectHelper.createJavaProject(ui, PROJECT_NAME);
		JavaProjectHelper.createJavaClass(ui, PROJECT_NAME +"/src", "test", "Smoke");
						
		// Test EditorLocator used as a locator scope/parent
		
		ui.click(new SWTWidgetLocator(StyledText.class, new EditorLocator("Smoke.java")));
		//ui.pause(1000); //<-- for interactive verification
		
		// Test EditorLocator used to close a clean editor
		
		ui.ensureThat(new EditorLocator("Smoke.java").isClosed());
		//ui.click(new EditorLocator("Smoke.java", WT.CLOSE));
		ui.assertThat(new EditorLocator("Smoke.java").isVisible(false));
		
		
		String filePath = getExplorerPathTo("test/Smoke.java");
		
		ui.ensureThat(view("Package Explorer").isShowing());
		
		// Test EditorLocator used to close a dirty editor
		ui.contextClick(new TreeItemLocator(filePath,
					new ViewLocator("org.eclipse.jdt.ui.PackageExplorer")), "Open");
		
		
		ui.wait(ActiveEditorCondition.forName("Smoke.java"));

		ui.enterText("//comment");
		ui.keyClick(WT.CR);
//		ui.close(new EditorLocator("Smoke.java"));
		
		ui.click(new EditorLocator("Smoke.java", WT.CLOSE));

		ui.wait(new ShellShowingCondition("Save Resource"));
		ui.click(new ButtonLocator("Yes"));
		ui.wait(new ShellDisposedCondition("Save Resource"));

		//TODO: make the verification more robust... (e.g., get contents of the styled text and analyze)
	}

	private String getExplorerPathTo(String fileName) {
		String suffix = PROJECT_NAME +"/";
		IWidgetLocator[] found = getUI().findAll(new TreeItemLocator(suffix + fileName));
		if (found.length ==0)
			suffix += "src/";
		return suffix + fileName;
	}

}
