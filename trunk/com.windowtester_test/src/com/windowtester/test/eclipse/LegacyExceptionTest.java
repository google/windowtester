package com.windowtester.test.eclipse;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Button;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.swt.condition.eclipse.ConfirmPerspectiveSwitchShellHandler;
import com.windowtester.runtime.swt.condition.eclipse.ProjectExistsCondition;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.LegacyExceptionTest.WorkBenchHelper.View;
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
public class LegacyExceptionTest extends BaseTest {

	//consider moving this out for popular consumption
	static class WorkBenchHelper {

		public static enum View {
			BASIC_CONSOLE("(General|Basic)/Console",
					"org.eclipse.ui.console.ConsoleView"), BASIC_INTERNALWEBBROWSER(
					"(General|Basic)/Internal Web Browser",
					"org.eclipse.ui.internal.browser.WebBrowserView"), BASIC_NAVIGATOR(
					"(General|Basic)/Navigator",
					"org.eclipse.ui.views.ResourceNavigator"), BASIC_OUTLINE(
					"(General|Basic)/Outline",
					"org.eclipse.ui.views.ContentOutline"), BASIC_PROBLEMS(
					"(General|Basic)/Problems",
					"org.eclipse.ui.views.ProblemView"), BASIC_PROPERTIES(
					"(General|Basic)/Properties",
					"org.eclipse.ui.views.PropertySheet"), JAVA_PACKAGEEXPLORER(
					"Java/Package Explorer",
					"org.eclipse.jdt.ui.PackageExplorer"), OTHER_DRAW2DDNDVIEW(
					"Other/WT SMOKE: Draw 2D DND View",
					"com.collab.com.collab.wt.smoke.dndDraw2DView"), OTHER_TEXTFIELDTESTVIEW(
					"Other/WT SMOKE: Text Test View",
					"com.collab.wt.smoke.textfieldTestView"), PDERUNTIME_ERRORLOG(
					"PDE Runtime/Error Log", "org.eclipse.pde.runtime.LogView");

			private String _viewSelectionPath;

			private String _viewID;

			private View(String label, String viewID) {
				_viewSelectionPath = label;
				_viewID = viewID;
			}

			public String getViewID() {
				return _viewID;
			}

			@Override
			public String toString() {
				return _viewSelectionPath;
			}
		}

		/**
		 * createSimpleProject - Create a project with the default facets (if any)
		 *    providing only a project name. Method should wait until the project is
		 *    created
		 * 
		 * @param ui - Driver for UI generated input
		 * @param projectName - Should adhere to project name validation rules (not 
		 *     null, not the empty string, legal characters, etc)
		 */
		public void createSimpleProject(IUIContext ui, String projectName)
				throws WidgetSearchException {
			try{
			TypingLinuxHelper.switchToInsertStrategyIfNeeded();
			TestCase.assertNotNull(ui);
			TestCase.assertNotNull(projectName);

			//listen for perspective changes
			IShellMonitor sm = ((IShellMonitor) ui
					.getAdapter(IShellMonitor.class));
			IShellConditionHandler perspectiveChangeHandler = new ConfirmPerspectiveSwitchShellHandler(
					true);
			sm.add(perspectiveChangeHandler);

			ui.click(new MenuItemLocator(
//					"&File/&New\t(Shift\\+Alt|Alt\\+Shift)\\+N/P&roject...")); //linux safe
					"File/New/Project..."));
			ui.wait(new ShellShowingCondition("New Project"));
			ui.click(new TreeItemLocator("(Simple|General)/Project")); //3.* safe path
			ui.click(new ButtonLocator("&Next >"));
			ui.click(new LabeledTextLocator("&Project name:"));
			ui.enterText(projectName);
			ui.click(new ButtonLocator("&Finish"));

			//wait for the project creation dialog to be dismissed
			ui.wait(new ShellDisposedCondition("New Project"));

			waitForProjectExists(ui, projectName, true);

			//stop listening for perspective changes
			sm.remove(perspectiveChangeHandler);
			}finally{
				TypingLinuxHelper.restoreOriginalStrategy();
			}
		}

		/**
		 * openView - Open the view of the given type
		 * 
		 * @param ui - Driver for UI generated input
		 * @param type - Type to open.
		 */
		public void openView(IUIContext ui, View type)
				throws WidgetSearchException {
			TestCase.assertNotNull(ui);
			TestCase.assertNotNull(type);

			ui.wait(TimeElapsedCondition.milliseconds(500));
			ui.click(new MenuItemLocator("&Window/Show &View/&Other.*")); //3.* safe path
			ui.wait(new ShellShowingCondition("Show View"));
			ui.click(new TreeItemLocator(type.toString()));
			ui.click(new ButtonLocator("OK"));
		}

		/**
		 * Wait for the project with the given name to exist
		 * 
		 * @param ui - Driver for UI generated input
		 * @param projectName - Should not be null
		 * @param exists - True if the project should exist for this
		 *    condition to be met
		 */
		public void waitForProjectExists(IUIContext ui, String projectName,
				boolean exists) {
			ui.wait(new ProjectExistsCondition(projectName, exists), 45000,
					2500);
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void oneTimeSetup() throws Exception {
		closeWelcomePageIfNecessary();
		WorkBenchHelper helper = new WorkBenchHelper();
		helper.createSimpleProject(getUI(), getTestProjectName());
		helper.openView(getUI(), View.JAVA_PACKAGEEXPLORER);
	}

	private String getTestProjectName() {
		return getClass().getName() + "Project";
	}

	public void testTreeClick() throws WidgetSearchException {
		IUIContext ui = getUI();
		try {
			ui.click(new TreeItemLocator("BogusTreeItem", new ViewLocator(View.JAVA_PACKAGEEXPLORER.getViewID())));
			fail("should have thrown an exception!");
		} catch (WidgetNotFoundException e) {
			//pass!
		}
	}

	public void testContexTreeClick() throws WidgetSearchException {
		IUIContext ui = getUI();
		try {
			ui.contextClick(new TreeItemLocator("BogusTreeItem", new ViewLocator(View.JAVA_PACKAGEEXPLORER.getViewID())), "Bogus/Path");
			fail("should have thrown an exception!");
		} catch (WidgetNotFoundException e) {
			//pass!
		}
	}
	
	
	public void testBasicClick() {
		try {
			getUI().click(new SWTWidgetLocator(Button.class, "BogusButton"));
			fail("should have thrown an exception!");
		} catch (WidgetSearchException e) {
			// pass!
		}
	}

}
