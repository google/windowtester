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
package com.windowtester.eclipse.ui.wizard;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.osgi.framework.Bundle;

import com.windowtester.eclipse.ui.dialogs.ExceptionDetailsDialog;
import com.windowtester.ui.util.Logger;

/**
 * Wizard for creating a new example project in the workspace
 */
public class NewExampleProjectWizard extends Wizard
	implements INewWizard, IImportWizard, IExecutableExtension
{
	private String bundleId;
	private String zipPath;
	private NewExampleProjectWizardPage page;

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
		throws CoreException
	{
		/* $codepro.preprocessor.if version >= 3.2 $ */
		bundleId = config.getContributor().getName();

		/* $codepro.preprocessor.elseif version == 3.1 $ 
		 bundleId = config.getNamespace();

		 $codepro.preprocessor.elseif version < 3.1 $ 
		 bundleId = config.getDeclaringExtension().getNamespace();
		 
		 $codepro.preprocessor.endif $ */

		if (data instanceof String) {
			zipPath = "examples/" + ((String) data) + "-src.zip";
		}
		else {
			Logger.logStackTrace("Expected data to be name of zip file containing example projects, but found " + data);
		}
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	public void addPages() {
		setWindowTitle("New Project(s)");
		page = new NewExampleProjectWizardPage(bundleId, zipPath);
		addPage(page);
	}

	public boolean performFinish() {
		try {
			getContainer().run(true, true, new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
					InterruptedException
				{
					monitor.beginTask("Extracting new example projects", 2);
					try {
						unzipProjects(new SubProgressMonitor(monitor, 1));
						importProjects(new SubProgressMonitor(monitor, 1));
					}
					catch (IOException e) {
						throw new InvocationTargetException(e);
					}
					monitor.done();
				}
			});
		}
		catch (InvocationTargetException e) {
			String errMsg = "Failed create example projects from " + zipPath + " in bundle " + bundleId;
			Logger.log(errMsg, e);
			new ExceptionDetailsDialog(getShell(), "Import Failed", errMsg, e).open();
			return false;
		}
		catch (InterruptedException e) {
			// Canceled by user
			return false;
		}
		return true;
	}

	private void unzipProjects(IProgressMonitor monitor) throws IOException {
		Bundle bundle = Platform.getBundle(bundleId);
		URL zipUrl = bundle.getEntry(zipPath);
		InputStream input = zipUrl.openStream();
		try {
			unzipProjects(input, monitor);
		}
		finally {
			input.close();
		}
	}

	private void unzipProjects(InputStream input, IProgressMonitor monitor) throws IOException {
		monitor.beginTask("Unzipping projects", 100);
		IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		ZipInputStream zip = new ZipInputStream(input);
		while (true) {
			if (monitor.isCanceled())
				throw new OperationCanceledException();
			ZipEntry entry = zip.getNextEntry();
			if (entry == null)
				break;
			String relPath = entry.getName();
			if (relPath.endsWith("/"))
				continue;
			File outputFile = rootPath.append(relPath).toFile();
			outputFile.getParentFile().mkdirs();
			OutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
			try {
				byte[] buf = new byte[1024];
				while (true) {
					int count = zip.read(buf);
					if (count == -1)
						break;
					output.write(buf, 0, count);
				}
				monitor.worked(1);
			}
			finally {
				output.close();
			}
		}
		monitor.done();
	}

	private void importProjects(IProgressMonitor monitor) throws CoreException {
		if (monitor.isCanceled())
			throw new OperationCanceledException();
		String[] projectNames = page.getProjectNames();
		monitor.beginTask("Import projects", projectNames.length * 2);
		for (int i = 0; i < projectNames.length; i++) {
			if (monitor.isCanceled())
				throw new OperationCanceledException();
			importProject(projectNames[i], monitor);
		}
		monitor.done();
	}

	private void importProject(String projectName, IProgressMonitor monitor) throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject project = workspace.getRoot().getProject(projectName);
		IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocation(null);
		project.create(description, new SubProgressMonitor(monitor, 1));
		project.open(new SubProgressMonitor(monitor, 1));

		// Direct ECLIPSE_HOME references are different each Eclipse installation
		// so adjust the classpath accordingly
		
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] classpath = javaProject.getRawClasspath();
		boolean modified = false;
		for (int i = 0; i < classpath.length; i++) {
			IClasspathEntry entry = classpath[i];
			if (entry.getEntryKind() != IClasspathEntry.CPE_VARIABLE)
				continue;
			IPath path = entry.getPath();
			if (path.segmentCount() != 3)
				continue;
			if (!path.segment(0).equals("ECLIPSE_HOME"))
				continue;
			if (!path.segment(1).equals("plugins"))
				continue;
			String jarName = path.segment(2);
			path = path.removeLastSegments(1);
			IPath pluginsPath = JavaCore.getResolvedVariablePath(path);
			if (pluginsPath == null) {
				Logger.log("Failed to resolve " + path);
				continue;
			}
			File pluginsDir = pluginsPath.toFile();
			String jarPrefix = jarName.substring(0, jarName.indexOf('_') + 1);
			String[] childNames = pluginsDir.list();
			if (childNames == null) {
				Logger.log("Failed to obtain children for " + pluginsDir.getPath());
				continue;
			}
			for (int j = 0; j < childNames.length; j++) {
				String name = childNames[j];
				if (name.startsWith(jarPrefix)) {
					modified = true;
					classpath[i] = JavaCore.newVariableEntry(path.append(name), null, null);
					break;
				}
			}
		}
		if (modified)
			javaProject.setRawClasspath(classpath, new NullProgressMonitor());
	}
}
