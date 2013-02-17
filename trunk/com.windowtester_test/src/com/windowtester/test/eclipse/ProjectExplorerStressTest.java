package com.windowtester.test.eclipse;

import static com.windowtester.runtime.swt.internal.abbot.TreeItemTester.NO_CHILDREN_EXPANSION_ERROR_MSG_DETAIL;
import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.eclipse.ProjectExistsCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.condition.eclipse.DirtyEditorCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
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
public class ProjectExplorerStressTest extends UITestCaseSWT {

//	private static final String FAILED_TO_FIND_ERROR_MSG_DETAIL = "Failed to find match for";
	private static final String DELETE_RESOURCES_SHELL_TITLE = EclipseUtil.isAtLeastVersion_34() ? "Delete Resources" : "Confirm Project Delete";
	private static final String DELETE_RESOURCES_SHELL_ACCEPT_BUTTON_TEXT = EclipseUtil.isAtLeastVersion_34() ? "OK" : "Yes";
	
	
	private static final String PROJECT_EXPLORER_VIEW_CATEGORY = "(Simple|General)";

	private class LifeCycleHelper {
		//useful for tearDown
		protected void saveAllIfNecessary() throws WidgetSearchException {
			if (anyUnsavedChanges())
				getUI().click(new MenuItemLocator("File/Save All"));
		}

		private boolean anyUnsavedChanges() {
			return new DirtyEditorCondition().test();
		}
		
		protected void closeWelcomePageIfNecessary() throws Exception {
			getUI().ensureThat(view("Welcome").isClosed());
		}
		
	}
	
	
	private class TestProjectManager {
	
		private int projectIndex = 0;
		
		
		void createNewProject() throws WidgetSearchException {
			createSimpleProject(getUI(), getFreshProjectName());
		}

		private String getFreshProjectName() {
			return getProjectNamePrefix() + nextIndex();
		}	
		
//		private String getLastProjectName() {
//			return getProjectNamePrefix() + lastIndex();
//		}	
		
		private int nextIndex() {
			return projectIndex++;
		}

//		private int lastIndex() {
//			return projectIndex;
//		}
		
		private String getProjectNamePrefix() {
			return ProjectExplorerStressTest.class.getName() + "Project";
		}

		void createSimpleProject(IUIContext ui, String projectName) throws WidgetSearchException {
			try{
			TypingLinuxHelper.switchToInsertStrategyIfNeeded();
			ensureNotNull(ui, projectName);
			
			ui.click(new MenuItemLocator("File/New/Project..."));
			ui.wait(new ShellShowingCondition("New Project"));
			ui.click(new TreeItemLocator("(Simple|General)/Project"));
			ui.click(new ButtonLocator("Next >"));
			ui.assertThat(new ButtonLocator("Finish").isEnabled(false));
			ui.enterText(projectName);
			ui.assertThat(new ButtonLocator("Finish").isEnabled(true));
			ui.click(new ButtonLocator("Finish"));
			ui.wait(new ShellDisposedCondition("New Project"));
			ui.wait(new JobsCompleteCondition());
			ui.wait(new SWTIdleCondition());
			}finally{
				TypingLinuxHelper.restoreOriginalStrategy();
			}
		}
		
//		void closeProject(String projectName) throws WidgetSearchException {
//			IUIContext ui = getUI();
//			ui.contextClick(projectTree(projectName), "Close Project");
//			ui.wait(new ShellDisposedCondition("Close Project"));
//			//TODO: wait for closed project condition...
//		}
		
		void deleteProject(String projectName) throws WidgetSearchException {
			IUIContext ui = getUI();
			ui.contextClick(projectTree(projectName), "Delete");
			ui.wait(new ShellShowingCondition(DELETE_RESOURCES_SHELL_TITLE));
			ui.click(new ButtonLocator(DELETE_RESOURCES_SHELL_ACCEPT_BUTTON_TEXT));
			ui.wait(new ShellDisposedCondition(DELETE_RESOURCES_SHELL_TITLE));
			ui.wait(new ProjectExistsCondition(projectName, false));
		}

		void deleteAll() throws Exception {
			ensureProjectViewIsVisible();
			eachDo(new ProjectAction() {
				public void run(String projectName) throws WidgetSearchException {
					deleteProject(projectName);
				}				
			});
		}

		void eachDo(ProjectAction projectAction) throws Exception {
			for (int i=0; i < projectIndex; ++i)
				projectAction.run(getProjectNamePrefix() + i);
		}
		
		
	}

	
	private static interface ProjectAction {
		void run(String projectName) throws Exception;
	}
	

	private static final String PROJECT_EXPLORER_VIEW_NAME = "Project Explorer";
	private static final String PROJECT_EXPLORER_VIEW_ID   = "org.eclipse.ui.navigator.ProjectExplorer";
	
	private final TestProjectManager projectManager = new TestProjectManager();
	private final LifeCycleHelper lifecycleHelper = new LifeCycleHelper();
	
	
	//////////////////////////////////////////////////////////////////////////
	//
	// Tests
	//
	//////////////////////////////////////////////////////////////////////////
	
	public void XtestCreateMultipleSimpleProjects() throws Exception {
		createProjects(5);
		eachProjectDo(new ProjectAction() {
			public void run(String projectName) throws WidgetSearchException {
				clickProjectTree(projectName + "/.project");
			}
		});
	}


	public void testClickNonExistentProjectContent() throws Exception {
		createProjects(1);
		eachProjectDo(new ProjectAction() {
			public void run(String projectName) throws WidgetSearchException {
				String treePath = projectName + "/.project";
				try {
					clickProjectTree(treePath);
					fail("Expected WidgetSearchException");
				}
				catch (WidgetSearchException e) {
					assertExceptionMessage("testClickNonExistentProjectContent", e,
						NO_CHILDREN_EXPANSION_ERROR_MSG_DETAIL, "No tree items found for '");
				}
			}
		});
	}

	private void assertExceptionMessage(String testMethodName, Exception e, String w1ExMsg, String w2ExMsg)
	{
		String message = e.getMessage();
		System.err.println("--- " + testMethodName);
		e.printStackTrace();
		if (!message.contains(w1ExMsg)
			&& !message.contains(w2ExMsg))
			fail(("message should have contained: " + w1ExMsg + "\n   or: "
				+ w2ExMsg + "\n   but got: " + message));
	}
	
	
	public void testClickNonExistentProjectInEmptyTree() throws Exception {
		try {
			clickProjectTree("NonExistent");
			fail("Expected WidgetSearchException");
		} catch(WidgetNotFoundException e) {
			assertExceptionMessage("testClickNonExistentProjectInEmptyTree", e, "Item: [NonExistent] not found",
				"No tree items found for 'NonExistent' in");
		}
	}

	public void testClickNonExistentProjectInPopulatedTree() throws Exception {
		createProjects(1);
		try {
			clickProjectTree("NonExistent");
			fail("Expected WidgetSearchException");
		} catch(WidgetNotFoundException e) {
			assertExceptionMessage("testClickNonExistentProjectInPopulatedTree", e, "Item: [NonExistent] not found",
				"No tree items found for 'NonExistent' in");
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	//
	// Lifecycle
	//
	//////////////////////////////////////////////////////////////////////////
	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		lifecycleHelper.saveAllIfNecessary();
		projectManager.deleteAll();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		lifecycleHelper.closeWelcomePageIfNecessary();
	}

	//////////////////////////////////////////////////////////////////////////
	//
	// Utils
	//
	//////////////////////////////////////////////////////////////////////////
	
	private void eachProjectDo(ProjectAction projectAction) throws Exception {
		projectManager.eachDo(projectAction);
	}
	
	protected void clickProjectTree(String treePath) throws WidgetSearchException {
		ensureProjectViewIsVisible();
		getUI().click(projectTree(treePath));
	}


	private void createProjects(int numProjects) throws WidgetSearchException {
		for (int i=0; i < numProjects; ++i) 
			projectManager.createNewProject();
	}

	
	void showProjectExplorer() throws WidgetSearchException {
		openView(PROJECT_EXPLORER_VIEW_CATEGORY + "/" + PROJECT_EXPLORER_VIEW_NAME);
	}
	
	void openView(String viewPath) throws WidgetSearchException {
    	ensureNotNull(viewPath);
    	IUIContext ui = getUI();
        ui.click(new MenuItemLocator("&Window/Show &View/&Other.*")); //3.* safe path
        ui.wait(new ShellShowingCondition("Show View"));
        ui.click(new TreeItemLocator(viewPath));
        ui.click(new ButtonLocator("OK"));
        ui.wait(new ShellDisposedCondition("Show View"));
    }

	void ensureProjectViewIsVisible() throws WidgetSearchException {
    	if (isProjectViewOpen())
    		return; //already open
    	showProjectExplorer();
	}
	
	void closeView(String viewName) throws WidgetSearchException {
    	ensureNotNull(viewName);
    	if (!(isProjectViewOpen()))
    		return; //already closed
    	IUIContext ui = getUI();
    	ui.contextClick(new CTabItemLocator(viewName), "Close");	
    	ui.wait(projectViewClosed());
    }


	private ICondition projectViewClosed() {
		return new ViewLocator(PROJECT_EXPLORER_VIEW_ID).isVisible(false);	
	}

	private boolean isProjectViewOpen() throws WidgetSearchException {
		return new ViewLocator(PROJECT_EXPLORER_VIEW_ID).isVisible(getUI());
	}
	
    void ensureNotNull(Object ... args) {
		for (Object arg : args)
			assertNotNull(arg);
	}
	
    static ILocator projectTree(String treePath) {
    	return new TreeItemLocator(treePath, new ViewLocator(PROJECT_EXPLORER_VIEW_ID));
    }
    
	
}
