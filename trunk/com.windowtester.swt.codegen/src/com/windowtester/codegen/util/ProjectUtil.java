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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;

public class ProjectUtil {
	/**
	 * Create Project in specified location. If location is null the default will be used
	 * 
	 * @param project
	 * @param locationPath
	 * @param monitor
	 * @throws CoreException
	 */
	public static void createProject(IProject project, IPath locationPath, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}				
		monitor.beginTask("Creating a project.", 10); 

		// create the project
		try {
			if (!project.exists()) {
				IProjectDescription desc= project.getWorkspace().newProjectDescription(project.getName());
				if (Platform.getLocation().equals(locationPath)) {
					locationPath= null;
				}
				desc.setLocation(locationPath);
				project.create(desc, monitor);
				monitor= null;
			}
			if (!project.isOpen()) {
				project.open(monitor);
				monitor= null;
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}
	
	public static void addNature(IProject project, String natureId, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		if (!project.hasNature(natureId)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures= description.getNatureIds();
			String[] newNatures= new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = natureId;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		} else {
			monitor.worked(1);
		}
	}
	
	public static void addDefaultEntries(IJavaProject project, IFolder source, IProgressMonitor monitor) throws CoreException{
		
		IFolder bin = project.getProject().getFolder(new Path("bin"));
		
		List cpEntries= new ArrayList();
		cpEntries.add(JavaCore.newSourceEntry(source.getFullPath()));
		cpEntries.addAll(Arrays.asList(getDefaultClasspathEntry()));
		IClasspathEntry[] entries= (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
		
		if(!source.exists()){
			source.getLocation().toFile().mkdirs();
		}
		if(!bin.exists()){
			bin.getLocation().toFile().mkdirs();
		}
		project.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		project.setRawClasspath(entries, bin.getFullPath(), monitor);
	}
	
	public static IClasspathEntry[] getDefaultClasspathEntry() {
		return PreferenceConstants.getDefaultJRELibrary();
	}
}
