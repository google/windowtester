package com.windowtester.test.eclipse.locator;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import org.eclipse.core.runtime.Path;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.eclipse.WorkbenchLocator;
import com.windowtester.test.eclipse.helpers.SimpleProjectHelper;

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
public class NavigatorDoubleClickTest extends UITestCaseSWT { 
	
    private static final String PROJECT_NAME = NavigatorDoubleClickTest.class.getName() + "Project";
    private static final String FOLDER_NAME  = "Test Folder";
    private static final String FILE_NAME    = "Test.txt";
	
    /* @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        IUIContext ui = getUI();
        ui.ensureThat(new WorkbenchLocator().hasFocus());
        ui.ensureThat(ViewLocator.forName("Welcome").isClosed());
    }

    public void testNavigatorDoubleClick() throws Exception {
        IUIContext ui = getUI();
        
        SimpleProjectHelper.createSimpleProject(ui, PROJECT_NAME);
        SimpleProjectHelper.createFolder(ui, new Path(PROJECT_NAME), FOLDER_NAME);   
        SimpleProjectHelper.createSimpleFile(ui, new Path(PROJECT_NAME + "/" + FOLDER_NAME), FILE_NAME);   
        
        ui.ensureThat(new EditorLocator(FILE_NAME).isClosed());
        ui.ensureThat(view("Project Explorer").isShowing());
        ui.click(2, new TreeItemLocator(
        		PROJECT_NAME + "/" + FOLDER_NAME + "/" + FILE_NAME, new ViewLocator(
                "org.eclipse.ui.navigator.ProjectExplorer"))/*, WT.SHIFT*/);
        ui.assertThat(new EditorLocator(FILE_NAME).isActive());
        
    }

}
