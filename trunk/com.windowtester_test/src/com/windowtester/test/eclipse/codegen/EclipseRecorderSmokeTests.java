package com.windowtester.test.eclipse.codegen;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.EclipseUtil;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper.Perspective;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper.View;

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
public class EclipseRecorderSmokeTests extends AbstractRecorderSmokeTest
{
	
	@Override
	protected void setUp() throws Exception {
		closeWelcomePageIfNecessary();
		openJavaPerspective();
		openPackageExplorer();
		super.setUp(); //<-- NOW START RECORDING!
	}
	
	private void openPackageExplorer() throws WidgetSearchException {
		WorkBenchHelper.openView(getUI(), View.JAVA_PACKAGEEXPLORER);
	}

	private void openJavaPerspective() throws WidgetSearchException {
		WorkBenchHelper.openPerspective(getUI(), Perspective.JAVA);
	}

	public void testViewMenu() throws Exception {
		IUIContext ui = getUI();
		
		// View menu locator not implemented in Eclipse 3.1
		if (EclipseUtil.isVersion_31())
			return;
		
		ui.click(new MenuItemLocator("Window/Show View/Package Explorer"));
		ui.click(new PullDownMenuItemLocator("Filters...", new ViewLocator("org.eclipse.jdt.ui.PackageExplorer")));
		ui.wait(new ShellShowingCondition("Java Element Filters"));
		ui.click(new ButtonLocator("&Name filter patterns (matching names will be hidden):"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Java Element Filters"));
	}
	
	public void testRunToolItemPullDown() throws Exception {
		IUIContext ui = getUI();
		String menuPath = EclipseUtil.isVersion_32() ? "Ru&n..." : "Open Run Dialog...";
		//ugh 3.4...
		if (EclipseUtil.isAtLeastVersion_34())
			menuPath = "Run Configurations...";
		ui.click(new PullDownMenuItemLocator(menuPath, new ContributedToolItemLocator("org.eclipse.debug.internal.ui.actions.RunDropDownAction")));
		ui.wait(new ShellShowingCondition("Run( Configurations)?")); //3.4M7+
		ui.click(new ButtonLocator("Close"));
		ui.wait(new ShellDisposedCondition("Run( Configurations)?")); //3.4M7+
	}
	
	
	public void testJavaProjectClickInPackageExplorer() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("EclipseRecorderSmokeTest_JPClickTestProject");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		ui.click(new TreeItemLocator("EclipseRecorderSmokeTest_JPClickTestProject", new ViewLocator("org.eclipse.jdt.ui.PackageExplorer")));
		ui.wait(TimeElapsedCondition.milliseconds(2000));//give the recorder a moment to catch the click
	}
	
	public void testCTabItemClose() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Error Log"));
		ui.ensureThat(new CTabItemLocator("Error Log").isClosed());
	}

}
