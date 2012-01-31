/**
 * 
 */
package com.windowtester.test.eclipse.helpers;

import static com.windowtester.runtime.swt.locator.SWTLocators.treeItem;
import junit.framework.TestCase;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.eclipse.PerspectiveActiveCondition;
import com.windowtester.runtime.swt.condition.eclipse.ViewShowingCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

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
public class WorkBenchHelper {

	private static final String PREFERENCES_MENU_ITEM_PATH = "Window/&Preferences(...)?"; //3.4M7+-safe
	private static final String HELP_VIEW_ID = "org.eclipse.help.ui.HelpView";
	
	public static enum View {
		BASIC_CONSOLE("(General|Basic)/Console",
				"org.eclipse.ui.console.ConsoleView"), BASIC_INTERNALWEBBROWSER(
				"(General|Basic)/Internal Web Browser",
				"org.eclipse.ui.browser.view" /*3.3*/), BASIC_NAVIGATOR(
				"(General|Basic)/Navigator",
				"org.eclipse.ui.views.ResourceNavigator"), BASIC_OUTLINE(
				"(General|Basic)/Outline",
				"org.eclipse.ui.views.ContentOutline"), BASIC_PROBLEMS(
				"(General|Basic)/Problems", "org.eclipse.ui.views.ProblemView"), BASIC_PROPERTIES(
				"(General|Basic)/Properties",
				"org.eclipse.ui.views.PropertySheet"), JAVA_PACKAGEEXPLORER(
				"Java/Package Explorer", "org.eclipse.jdt.ui.PackageExplorer"), 
				//PDERUNTIME_ERRORLOG("PDE Runtime/Error Log", "org.eclipse.pde.runtime.LogView"), 
			JUNIT("Java/JUnit", "org.eclipse.jdt.junit.ResultView");

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
		
		public ViewLocator locator(){
			return ViewLocator.forId(getViewID());
		}
		
	}

	// ------------------------------------------------------------------------
	// PERSPECTIVES AVAILABLE TO OPEN
	// ------------------------------------------------------------------------
	public static enum Perspective {
		// Wildcards are used because the "(default)" text is appended to
		//    the perspective name depending on the product definition
		DEBUG("Debug", "org.eclipse.debug.ui.DebugPerspective"), JAVA(
				"Java( \\(default\\))?", "org.eclipse.jdt.ui.JavaPerspective"),
		/* !pq: Disabling in case (mine!) wtp is not installed: 
		 * J2EE("J2EE", "org.eclipse.jst.j2ee.J2EEPerspective"),
		 */
		RESOURCE("Resource.*", "org.eclipse.ui.resourcePerspective"),
		TEAM("Team", "org.eclipse.team.ui.TeamSynchronizingPerspective"),
		CVS_REPO_EXPLORING("CVS Repository Exploring", "org.eclipse.team.cvs.ui.cvsPerspective"),
		JAVA_BROWSING("Java Browsing", "org.eclipse.jdt.ui.JavaBrowsingPerspective"),
		PDE("Plug-in Development", "org.eclipse.pde.ui.PDEPerspective");

		private String _label;
		private String _id;

		Perspective(String label, String id) {
			_label = label;
			_id = id;
		}

		public String getID() {
			return _id;
		}

		public String getLabel() {
			return _label;
		}

		@Override
		public String toString() {
			return _label;
		}
	};

	/**
	 * openView - Open the view of the given type
	 * 
	 * @param ui - Driver for UI generated input
	 * @param type - Type to open.
	 */
	public static void openView(IUIContext ui, WorkBenchHelper.View type)
			throws WidgetSearchException {
		TestCase.assertNotNull(ui);
		TestCase.assertNotNull(type);

		ui.click(new MenuItemLocator("&Window/Show &View/&Other.*")); //3.* safe path
		ui.wait(new ShellShowingCondition("Show View"));
		ui.click(new TreeItemLocator(type.toString()));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Show View"));
	}

	/**
	 * openPerspective - Open the perspective of the given type
	 * 
	 * @param type - Type to open.
	 * @throws WidgetSearchException 
	 */
	public static void openPerspective(IUIContext ui, Perspective type)
			throws WidgetSearchException {
		TestCase.assertNotNull(ui);
		TestCase.assertNotNull(type);

		ui.click(new MenuItemLocator("Window/Open Perspective/Other..."));
		ui.wait(new ShellShowingCondition("(Open|Select) Perspective")); //3.* safe path
		ui.click(new TableItemLocator(type.getLabel()));
		ui.click(new ButtonLocator("OK"));

		ui.wait(new PerspectiveActiveCondition(type.getID()));
	}

	
	public static void openPreferences(IUIContext ui) throws WidgetSearchException {
		if (OS.isOSX())
			ui.keyClick(WT.COMMAND, ',');
		else
			ui.click(new MenuItemLocator(PREFERENCES_MENU_ITEM_PATH));
		ui.wait(new ShellShowingCondition("Preferences"));
	}
	
	public static void openDynamicHelp(IUIContext ui) throws WidgetSearchException {
		if (OS.isOSX()){
			DisplayReference.getDefault().execute(new VoidCallable(){
				public void call() throws Exception {
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					ActionFactory.DYNAMIC_HELP.create(window).run();
				}
			});
		} else {
			ui.click(new MenuItemLocator("Help/Dynamic Help"));
		}
		ui.wait(new ViewShowingCondition(HELP_VIEW_ID));
		ui.wait(new JobsCompleteCondition());

	}
	
	
	public static void openPreferencePage(IUIContext ui, String pagePath) throws WidgetSearchException {
		openPreferences(ui);
		ui.click(treeItem(pagePath));
	}
	
	
}