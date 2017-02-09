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
package com.windowtester.test.eclipse;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;
import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.workbench;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.condition.eclipse.DirtyEditorCondition;
import com.windowtester.runtime.swt.locator.MenuItemLocator;


/**
 * Collect a few test-dependent helper methods.
 * 
 * @author Steve Messick
 * @author Phil Quitslund
 */
public class BaseTest extends UITestCaseSWT {

	protected void setUp() throws Exception {
		WT.setLocaleToCurrent();
		checkForPDERequirement();
		ensureWorkbenchIsInFront();
		closeWelcomePageIfNecessary();
	}

	protected void ensureWorkbenchIsInFront() throws WaitTimedOutException, Exception {
		getUI().ensureThat(workbench().hasFocus());
	}


	private void checkForPDERequirement() {
		assertTrue("This test must be run as a PDE test", org.eclipse.core.runtime.Platform.isRunning());
	}


	//useful for tearDown
	protected void saveAllIfNecessary() throws WidgetSearchException {
		if (anyUnsavedChanges())
			getUI().click(new MenuItemLocator("File/Save All"));
	}

	private boolean anyUnsavedChanges() {
		return new DirtyEditorCondition().test();
	}
	
	/**
	 * Recent versions of Eclipse do not close the welcome page when view
	 * is opened. Make sure it gets closed.
	 * @throws WaitTimedOutException 
	 */
	protected void closeWelcomePageIfNecessary() throws Exception {
		getUI().ensureThat(view("Welcome").isClosed());

	}

	/**
	 * Assert that a project exists with the given name.
	 * @param projectName the name of the project to be tested
	 */
	public static void assertProjectExists(String projectName) {
		//TODO: convert to condition
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(projectName);
        assertTrue(project.exists());
	}

	/**
	 * Assert that a file exists at the given path.
	 * @param fillFilePath path to the file to be tested
	 */
	public static void assertFileExists(Path fullFilePath) {
		//TODO: convert to condition
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IFile file = root.getFile(fullFilePath);
        assertTrue(file.exists());
	}
}
