package com.windowtester.test.eclipse;

import static com.windowtester.test.eclipse.helpers.JavaProjectHelper.createJavaProject;
import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.openPerspective;
import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.openView;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.TreeLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper.Perspective;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper.View;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

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
public class BEAContextMenuTest extends BaseTest {

	public void testContextMenu() throws Exception {
		IUIContext ui = getUI();
		String projectName = getClass().getName();
		openPerspective(ui, Perspective.JAVA);
		createJavaProject(ui, projectName);
		ui.ensureThat(view("Package Explorer").isShowing());
		ui.contextClick(new TreeItemLocator(projectName, new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")), "New/Source Folder");
		ui.wait(new ShellShowingCondition("New Source Folder"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("New Source Folder"));
	}
	
	/**
	 * Port of repro: https://fogbugz.instantiations.com/default.php?32139
	 * <p>
	 * This test just fails when it tries to create a new project by
	 * right clicking in the ResourceNavigator view.
	 * @throws Exception
	 */
	public void testCRightClickNavigatorView() throws Exception {
		IUIContext ui = getUI();
		openView(ui, View.BASIC_NAVIGATOR);
		
		// TODO need a first class TreeLocator to replace this use of SWTWidgetLocator
		// so that contextClick can be implemented identical to TreeItemLocator
		// for this test to pass on Linux
		ui.contextClick(new TreeLocator( 
				new ViewLocator("org.eclipse.ui.views.ResourceNavigator")),
				"New/Project...");
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("New Project"));
	}

}
