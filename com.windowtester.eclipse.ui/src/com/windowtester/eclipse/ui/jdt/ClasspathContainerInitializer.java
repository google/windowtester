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
package com.windowtester.eclipse.ui.jdt;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.windowtester.codegen.util.BuildPathUtil;
import com.windowtester.codegen.util.RuntimeClasspathContainer;

/**
 * Initialize the WindowTester Runtime Classpath container
 */
public class ClasspathContainerInitializer extends org.eclipse.jdt.core.ClasspathContainerInitializer
{
	public ClasspathContainerInitializer() {
	}

	/**
	 * Binds a classpath container to a <code>IClasspathContainer</code> for a given
	 * project, or silently fails if unable to do so.
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#initialize(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject)
	 */
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
		Path path = new Path(BuildPathUtil.CLASSPATH_CONTAINER_ID);
		IClasspathEntry[] entries = BuildPathUtil.getRuntimeClasspathEntries(project);

		JavaCore.setClasspathContainer(path, new IJavaProject[]{
			project
		}, new IClasspathContainer[]{
			new RuntimeClasspathContainer(entries, path)
		}, null);
	}

	/**
	 * Returns <code>true</code> if this container initializer can be requested to
	 * perform updates on its own container values. If so, then an update request will be
	 * performed using
	 * {@link #requestClasspathContainerUpdate(IPath, IJavaProject, IClasspathContainer)}.
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#canUpdateClasspathContainer(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject)
	 */
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		return true;
	}

	/**
	 * Request a registered container definition to be updated according to a container
	 * suggestion. The container suggestion only acts as a place-holder to pass along the
	 * information to update the matching container definition(s) held by the container
	 * initializer. In particular, it is not expected to store the container suggestion as
	 * is, but rather adjust the actual container definition based on suggested changes.
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#requestClasspathContainerUpdate(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject, org.eclipse.jdt.core.IClasspathContainer)
	 */
	public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion)
		throws CoreException
	{
		JavaCore.setClasspathContainer(containerPath, new IJavaProject[]{
			project
		}, new IClasspathContainer[]{
			containerSuggestion
		}, null);
	}

	/**
	 * Returns a readable description for a container path. A readable description for a
	 * container path can be used for improving the display of references to container,
	 * without actually needing to resolve them.
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#getDescription(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject)
	 */
	public String getDescription(IPath containerPath, IJavaProject project) {
		return "Wintester classpath container";
	}

	/**
	 * Returns an object which identifies a container for comparison purpose. This allows
	 * to eliminate redundant containers when accumulating classpath entries (e.g. runtime
	 * classpath computation).
	 * 
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#getComparisonID(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.jdt.core.IJavaProject)
	 */
	public Object getComparisonID(IPath containerPath, IJavaProject project) {
		return containerPath;
	}
}
