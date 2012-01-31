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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.Bundle;

import com.swtdesigner.ResourceManager;
import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.ui.util.Logger;

/**
 * A wizard page informing the user that this wizard will create a new project in their
 * workspace.
 */
public class NewExampleProjectWizardPage extends WizardPage
{
	private final String bundleId;
	private final String zipPath;
	private String[] projectNames;
	private Label infoLabel;

	public NewExampleProjectWizardPage(String bundleId, String zipPath) {
		super("newExampleProjectPage");
		this.bundleId = bundleId;
		this.zipPath = zipPath;
		setTitle("Create New Example Project");
		setDescription("Create new example project(s) in your workspace");
		setImageDescriptor(ResourceManager.getPluginImageDescriptor(UiPlugin.getDefault(), "icons/full/wizban/new_wiz.png"));
	}

	/**
	 * Create contents of the wizard
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 20;
		gridLayout.marginHeight = 20;
		container.setLayout(gridLayout);
		//
		setControl(container);

		infoLabel = new Label(container, SWT.WRAP);
		final GridData gd_clickFinishToLabel = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		infoLabel.setLayoutData(gd_clickFinishToLabel);
		infoLabel.setText(getInfoText());

		updatePageComplete();
	}

	protected String getInfoText() {
		readProjectNames();
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		writer.println("Click the Finish button to create the following");
		writer.print("example project");
		if (projectNames != null && projectNames.length > 1)
			writer.print("s");
		writer.println(" in your workspace:");
		writer.println();
		writer.println();
		if (projectNames != null) {
			for (int i = 0; i < projectNames.length; i++) {
				writer.print("    * ");
				writer.println(projectNames[i]);
			}
		}
		writer.println();
		writer.println();
		writer.println("Click the Cancel button to abort this process");
		return stringWriter.toString();
	}

	/**
	 * Determine if the example can be created or if the workspace already has a project
	 * with that name.
	 */
	protected void updatePageComplete() {
		readProjectNames();
		if (projectNames == null || projectNames.length == 0) {
			setErrorMessage("Failed to read " + zipPath + " in bundle " + bundleId);
			setPageComplete(false);
			return;
		}
		for (int i = 0; i < projectNames.length; i++) {
			if (!verifyProjectDoesNotExist(projectNames[i])) {
				return;
			}
		}
		setErrorMessage(null);
		setPageComplete(true);
	}

	/**
	 * Check if the specified project exists
	 * 
	 * @param projectName the project name to be verified
	 * @return <code>true</code> if the project does NOT exist, else <code>false</code>
	 */
	protected boolean verifyProjectDoesNotExist(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if (project.exists()) {
			setErrorMessage("A project named " + projectName + " already exists in the workspace.");
			setPageComplete(false);
			return false;
		}
		IPath rootLoc = root.getLocation();
		File projectDir = rootLoc.append(projectName).toFile();
		if (projectDir.exists()) {
			setErrorMessage("A directory named " + projectName + " already exists in " + rootLoc);
			setPageComplete(false);
			return false;
		}
		return true;
	}

	/**
	 * Answer the project names read from the zip file
	 * 
	 * @return an array of project names or <code>null</code> if failed
	 */
	public String[] getProjectNames() {
		readProjectNames();
		return projectNames;
	}

	/**
	 * Read project names from the zip file if necessary
	 */
	private void readProjectNames() {
		if (projectNames != null || bundleId == null || zipPath == null)
			return;
		Bundle bundle = Platform.getBundle(bundleId);
		if (bundle == null) {
			Logger.log("[NewExampleProjectWizardPage] Failed to find bundle " + bundleId);
			return;
		}
		URL zipUrl = bundle.getEntry(zipPath);
		if (zipUrl == null) {
			Logger.log("[NewExampleProjectWizardPage] Failed to find " + zipPath + " in bundle " + bundleId);
			return;
		}
		InputStream input;
		try {
			input = zipUrl.openStream();
		}
		catch (IOException e) {
			Logger.log("[NewExampleProjectWizardPage] Failed to open " + zipPath + " in bundle " + bundleId, e);
			return;
		}
		try {
			readProjectNames(input);
		}
		catch (IOException e) {
			Logger.log("[NewExampleProjectWizardPage] Failed to read " + zipPath + " in bundle " + bundleId, e);
		}
		finally {
			try {
				input.close();
			}
			catch (IOException e) {
				Logger.log("[NewExampleProjectWizardPage] Failed to close " + zipPath + " in bundle " + bundleId, e);
			}
		}
	}

	/**
	 * Read project names from the zip stream
	 */
	private void readProjectNames(InputStream input) throws IOException {
		Collection result = new HashSet();
		ZipInputStream zip = new ZipInputStream(input);
		while (true) {
			ZipEntry entry = zip.getNextEntry();
			if (entry == null)
				break;
			String relPath = entry.getName();
			int index = relPath.indexOf("/");
			if (index == -1)
				break;
			result.add(relPath.substring(0, index));
		}
		projectNames = (String[]) result.toArray(new String[result.size()]);
		Arrays.sort(projectNames);
	}
}
