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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.windowtester.codegen.util.BuildPathUtil;
import com.windowtester.ui.util.Logger;

public class RuntimeClasspathContainerPage extends WizardPage implements
		IClasspathContainerPage, IClasspathContainerPageExtension {

	private IClasspathEntry[] realEntries;
	private TableViewer viewer;
	private Image projectImage;
	private Image libraryImage;
	private Image slibraryImage;
	private IClasspathEntry entry;
	private IJavaProject javaProject;
	private IClasspathEntry[] currentEntries;
	
	class EntryContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			if (realEntries != null)
				return realEntries;
			return new Object[0];
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}	
	
	class EntryLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getText(Object obj) {
			IClasspathEntry entry = (IClasspathEntry) obj;
			int kind = entry.getEntryKind();
			if (kind == IClasspathEntry.CPE_PROJECT)
				return entry.getPath().segment(0);
			IPath path = entry.getPath();
			String name = path.lastSegment();
			return name
				+ " - " //$NON-NLS-1$
				+ path.uptoSegment(path.segmentCount() - 1).toOSString();
		}
	
		public Image getImage(Object obj) {
			IClasspathEntry entry = (IClasspathEntry) obj;
			int kind = entry.getEntryKind();
			if (kind == IClasspathEntry.CPE_PROJECT)
				return projectImage;
			else if (kind == IClasspathEntry.CPE_LIBRARY) {
				IPath sourceAtt = entry.getSourceAttachmentPath();
				return sourceAtt!=null?slibraryImage:libraryImage;
			}
			return null;
		}
		public String getColumnText(Object obj, int col) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int col) {
			return getImage(obj);
		}
	}
	
	public RuntimeClasspathContainerPage(){
		super("Runtime Classpath Container");
		projectImage = PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
		libraryImage = JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_EXTERNAL_ARCHIVE);
		slibraryImage =	JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_EXTERNAL_ARCHIVE_WITH_SOURCE);
		setTitle("Window Tester Runtime Libraries");
		setDescription("This page shows the required Window Tester libraries that needed in your project");
	}

	public boolean finish() {
		try {
			entry = BuildPathUtil.getRuntimeContainerEntry(javaProject);
		} catch (JavaModelException e) {
			Logger.log(e);
		}
		return true;
	}

	public IClasspathEntry getSelection() {
		return entry;
	}

	public void setSelection(IClasspathEntry containerEntry) {
		this.entry = containerEntry;
		createRealEntries();
		if (viewer != null)
			initializeView();
	}
	
	private void createRealEntries() {
		realEntries = BuildPathUtil.getRuntimeClasspathEntries(javaProject);
		if (realEntries == null)
			realEntries = new IClasspathEntry[0];
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());
		Label requiredWintesterRuntimeLabel = new Label(container, SWT.NULL);
		requiredWintesterRuntimeLabel.setText("Required WinTester runtime:");
		viewer = new TableViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setContentProvider(new EntryContentProvider());
		viewer.setLabelProvider(new EntryLabelProvider());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 400;
		gd.heightHint = 300;
		viewer.getTable().setLayoutData(gd);
		Dialog.applyDialogFont(container);
		if (realEntries != null)
			initializeView();
		setControl(container);
	}

	private void initializeView() {
		viewer.setInput(currentEntries);
	}

	public void initialize(IJavaProject project, IClasspathEntry[] currentEntries) {
		this.javaProject = project;
		this.currentEntries = currentEntries;
	}

}
