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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

public class NewTargetFileWizard extends BasicNewFileResourceWizard {
    
    private static final String FILE_CREATION_ERROR_MSG = "An error occured creating the target file";

	private static final String DESCRIPTION = "Create a new WindowTester Recording Target";

	private static final String TITLE = "New Target";
	
	private WizardNewFileCreationPage mainPage;

    /**
     * Creates a wizard for creating a new file resource in the workspace.
     */
    public NewTargetFileWizard() {
        super();
    }

    /* (non-Javadoc)
     * Method declared on IWizard.
     */
    public void addPages() {
        //super.addPages(); //<--- notice we're overriding here
        mainPage = new NewTargetWizardPage("newFilePage1", getSelection());//$NON-NLS-1$
        mainPage.setTitle(TITLE);
        mainPage.setDescription(DESCRIPTION); 
        addPage(mainPage);
    }

    /* (non-Javadoc)
     * Method declared on IWorkbenchWizard.
     */
    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        super.init(workbench, currentSelection);
        setWindowTitle(TITLE);
        setNeedsProgressMonitor(true);
    }

    /* (non-Javadoc)
     * Method declared on IWizard.
     */
    public boolean performFinish() {
        IFile file = mainPage.createNewFile();
        if (file == null) {
			return false;
		}

        selectAndReveal(file);

        // Open editor on new file.
        IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
        try {
            if (dw != null) {
                IWorkbenchPage page = dw.getActivePage();
                if (page != null) {
                    IDE.openEditor(page, file, true);
                }
            }
        } catch (PartInitException e) {
            ErrorHelper.openError(dw.getShell(), FILE_CREATION_ERROR_MSG, 
                    e.getMessage(), e);
        }

        return true;
    }

    
}