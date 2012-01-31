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
package com.windowtester.eclipse.ui.target;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.windowtester.eclipse.ui.UiPlugin;

public class NewTargetWizardPage extends WizardNewFileCreationPage {

	
    private static final String ERROR_MSG_FILE_CREATION = "An error occured creating the target file";
	
	private static final String ERROR_MSG_STARTS_WITH_DOT = "A valid target file cannot start with a '.'";
	private static final String TARGET_EXTENSION = "target";
	private static final String ERROR_MSG_BAD_EXTENSION = "Target files must end in the \".target\" extension";
	
	private String fLastFilename;
	private final IStructuredSelection selection;

	/**
	 * @param pageName
	 * @param selection
	 */
	public NewTargetWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		this.selection = selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validatePage()
	 */
	protected boolean validatePage() {
		fLastFilename = getFileName().trim();

		// Verify the filename is non-empty
		if (fLastFilename.length() == 0) {
			// Reset previous error message set if any
			setErrorMessage(null);
			return false;
		}

		// Verify the file name does not begin with a dot
		if (fLastFilename.charAt(0) == '.') {
			setErrorMessage(ERROR_MSG_STARTS_WITH_DOT);
			return false;
		}

		if (!hasValidPageExtension(new Path(fLastFilename))) {
			setErrorMessage(ERROR_MSG_BAD_EXTENSION);
			return false;			
		}
		
		// Perform default validation
		return super.validatePage();
	}

	
	public static boolean hasValidPageExtension(IPath path){	
		String fileExtension = path.getFileExtension();	
		if(fileExtension == null)
			return false;
		return fileExtension.equalsIgnoreCase(TARGET_EXTENSION);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
	protected InputStream getInitialContents() {
		Object element = selection.getFirstElement();
		if (element instanceof IFile) {
			IFile file = (IFile)element;
			try {
				return Target.fromStream(file.getContents()).addRecordingPlugins().toStream();
			} catch (Exception e) {
				ErrorHelper.openError(getShell(), ERROR_MSG_FILE_CREATION, e.getMessage(), e);
			}
			return null;
		}
		
		return super.getInitialContents();
		
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validateLinkedResource()
	 */
	protected IStatus validateLinkedResource() {
		return new Status(IStatus.OK, UiPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createLinkTarget()
	 */
	protected void createLinkTarget() {
		// NO-OP
	}

	protected void createAdvancedControls(Composite parent) {
		// NO-OP
	}

	public String getFileName() {
		if (getControl() != null && getControl().isDisposed()) {
			return fLastFilename;
		}

		return super.getFileName();
	}
}
