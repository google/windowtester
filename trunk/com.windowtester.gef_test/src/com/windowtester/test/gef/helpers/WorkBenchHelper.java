package com.windowtester.test.gef.helpers;

import junit.framework.TestCase;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.eclipse.ConfirmPerspectiveSwitchShellHandler;
import com.windowtester.runtime.swt.condition.eclipse.ProjectExistsCondition;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.condition.eclipse.DirtyEditorCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;


/**
 * A collection of workbench-related macro routines.
 * 
 * <p>
 * Copyright (c) 2006, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class WorkBenchHelper {

	
    public static enum View
    {
        BASIC_CONSOLE
            ("(General|Basic)/Console",
             "org.eclipse.ui.console.ConsoleView"),
        BASIC_INTERNALWEBBROWSER
            ("(General|Basic)/Internal Web Browser",
             "org.eclipse.ui.internal.browser.WebBrowserView"),
        BASIC_NAVIGATOR
            ("(General|Basic)/Navigator",
             "org.eclipse.ui.views.ResourceNavigator"),
        BASIC_OUTLINE
            ("(General|Basic)/Outline",
             "org.eclipse.ui.views.ContentOutline"),
        BASIC_PROBLEMS
            ("(General|Basic)/Problems",
             "org.eclipse.ui.views.ProblemView"),
        BASIC_PROPERTIES
            ("(General|Basic)/Properties",
             "org.eclipse.ui.views.PropertySheet"),
        JAVA_PACKAGEEXPLORER
            ("Java/Package Explorer",
             "org.eclipse.jdt.ui.PackageExplorer"),
        OTHER_DRAW2DDNDVIEW
            ("Other/WT SMOKE: Draw 2D DND View",
             "com.collab.com.collab.wt.smoke.dndDraw2DView"),
        OTHER_TEXTFIELDTESTVIEW
            ("Other/WT SMOKE: Text Test View",
             "com.collab.wt.smoke.textfieldTestView"),
        PDERUNTIME_ERRORLOG
            ("PDE Runtime/Error Log",
             "org.eclipse.pde.runtime.LogView")
        ;
        
        private String _viewSelectionPath;
        private String _viewID;
        
        private View(String label, String viewID)
        {
            _viewSelectionPath = label;
            _viewID = viewID;
        }
        
        public String getViewID()
        {
            return _viewID;
        }
        
        @Override
        public String toString()
        {
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
        throws WidgetSearchException 
    {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);
        
        //listen for perspective changes
        IShellMonitor sm = ((IShellMonitor)ui.getAdapter(IShellMonitor.class));
        IShellConditionHandler perspectiveChangeHandler = new ConfirmPerspectiveSwitchShellHandler(true);
        sm.add(perspectiveChangeHandler);
        
        
        ui.click(new MenuItemLocator("&File/&New\t(Shift\\+Alt|Alt\\+Shift)\\+N/P&roject...")); //linux safe
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
        
    }

    /**
     * openView - Open the view of the given type
     * 
     * @param ui - Driver for UI generated input
     * @param type - Type to open.
     */
    public void openView(IUIContext ui, View type) throws WidgetSearchException
    {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(type);
        
        ui.pause(500);
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
    public void waitForProjectExists(IUIContext ui, 
                                     String projectName,
                                     boolean exists)
    {
        ui.wait(new ProjectExistsCondition(projectName, exists), 45000, 2500);
    }
	
	
	public static void saveAllIfNecessary(IUIContext ui) throws WidgetSearchException {
		if (anyUnsavedChanges())
			ui.click(new MenuItemLocator("File/Save All"));
	}
	
	private static boolean anyUnsavedChanges() {
		return new DirtyEditorCondition().test();
	}
	
    
}

