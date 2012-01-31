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

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Tests for the existence of a given file.
 *
 */
public class FileExistsCondition extends PathExistsCondition {
	
	/**
	 * Create an instance that tests a given file.
	 * @param file the file to test
	 * @param exists whether the file should exist or not
	 */
	public FileExistsCondition(IFile file, boolean exists) {
		super(file.getLocation(), exists);
	}

	/**
	 * Create an instance that tests a file at a given path.
	 * @param pathToFile the workspace-relative path to the file
	 * @param exists whether the file should exist or not
	 */
	public FileExistsCondition(IPath pathToFile, boolean exists) {
		super(ResourcesPlugin.getWorkspace().getRoot().getLocation().append(pathToFile), exists);
	}

	/**
	 * Create an instance that tests a given file.
	 * @param file the file to test
	 * @param exists whether the file should exist or not
	 */
	public FileExistsCondition(File file, boolean exists) {
		super(new Path(file.getAbsolutePath()), exists);
	}

}
