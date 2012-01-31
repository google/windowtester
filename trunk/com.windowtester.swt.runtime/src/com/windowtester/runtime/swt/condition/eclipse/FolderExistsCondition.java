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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 * Tests for the existence of a given folder.
 *
 */
public class FolderExistsCondition extends PathExistsCondition {
	
	/**
	 * Create an instance that tests a given folder.
	 * @param folder the folder to test
	 * @param exists whether the folder should exist or not
	 */
	public FolderExistsCondition(IFolder folder, boolean exists) {
		super(folder.getLocation(), exists);
	}

	/**
	 * Create an instance that tests a folder at a given path.
	 * @param pathToFolder the workspace-relative path to the folder
	 * @param exists whether the folder should exist or not
	 */
	public FolderExistsCondition(IPath pathToFolder, boolean exists) {
		super(ResourcesPlugin.getWorkspace().getRoot().getLocation().append(pathToFolder), exists);
	}

}
