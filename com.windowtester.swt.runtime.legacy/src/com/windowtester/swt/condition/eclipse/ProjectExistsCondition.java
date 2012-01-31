package com.windowtester.swt.condition.eclipse;

import org.eclipse.core.resources.IProject;

import com.windowtester.swt.condition.ICondition;

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
public class ProjectExistsCondition extends com.windowtester.runtime.swt.condition.eclipse.ProjectExistsCondition
	implements ICondition
{
	

	/**
	 * Create an instance that tests for the existence of a project specified 
	 * by name.
	 * @param projectName the name of the project to test
	 * @param exists whether the project should exist or not
	 */
	public ProjectExistsCondition(String projectName, boolean exists) {
		super(projectName, exists);
	}

	/**
	 * Create an instance that tests for the existence of a given project.
	 * @param project the project to test
	 * @param exists whether the project should exist or not
	 */
	public ProjectExistsCondition(IProject project, boolean exists) {
		super(project, exists);
	}

}