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
package com.windowtester.runtime.swt.condition.eclipse;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import com.windowtester.runtime.condition.ICondition;

/**
 * Tests for the existence of a given project.
 * 
 */
public class ProjectExistsCondition implements ICondition {
	
	private final IProject _project;
	private final boolean _exists;

	/**
	 * Create an instance that tests for the existence of a project specified 
	 * by name.
	 * @param projectName the name of the project to test
	 * @param exists whether the project should exist or not
	 */
	public ProjectExistsCondition(String projectName, boolean exists) {
		TestCase.assertNotNull(projectName);

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		TestCase.assertNotNull(root);

		_project = root.getProject(projectName);
		_exists = exists;
	}

	/**
	 * Create an instance that tests for the existence of a given project.
	 * @param project the project to test
	 * @param exists whether the project should exist or not
	 */
	public ProjectExistsCondition(IProject project, boolean exists) {
		TestCase.assertNotNull(project);

		_project = project;
		_exists = exists;
	}

	/**
	 * Test for the existence of the given project.
	 * 
	 * @see ICondition#test()
	 */
	public boolean test() {
		return _exists == _project.exists();
	}

	/**
	 * Return a String representation of this condition.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return " for project [" + _project.getName() + "] to exist: "
				+ _exists;
	}
}