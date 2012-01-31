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
package com.windowtester.codegen.util;

import org.eclipse.core.runtime.CoreException;

/**
 * A helper for updating project dependencies.
 *
 */
public interface IBuildPathUpdater {
	
	void addPluginDependency(String pluginId) throws CoreException;

}