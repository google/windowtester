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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;

public class RuntimeClasspathContainer implements IClasspathContainer {

	private IClasspathEntry[] entries;
	private IPath path;
	
	public RuntimeClasspathContainer(IClasspathEntry[] entries, IPath path){
		this.entries = entries;
		this.path = path;
	}
	
	public IClasspathEntry[] getClasspathEntries() {
		return entries;
	}

	public String getDescription() {
		return "WindowTester Runtime";
	}

	public int getKind() {
		return K_APPLICATION;
	}

	public IPath getPath() {
		return path;
	}

}
