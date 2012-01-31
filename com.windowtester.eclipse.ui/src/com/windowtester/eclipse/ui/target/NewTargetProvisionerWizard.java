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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.swt.codegen.wizards.NewElementWizard;

public class NewTargetProvisionerWizard extends NewElementWizard {
    
	private static final String TITLE = "WindowTester Target Provisioner";
	
	private NewTargetProvisionerPage mainPage;


    public NewTargetProvisionerWizard() {
        super();
        setDialogSettings(UiPlugin.getDefault().getDialogSettings());
    }

    /* (non-Javadoc)
     * Method declared on IWizard.
     */
    public void addPages() {
        //super.addPages(); //<--- notice we're overriding here
        mainPage = new NewTargetProvisionerPage("newFilePage1", getSelection(), getFileProvider());//$NON-NLS-1$
        mainPage.setWizard(this);
        addPage(mainPage);
    }

    private ITargetProvisionerFileProvider getFileProvider() {
    	return TargetProvisionerFileProvider.getProvider();
	}

	/* (non-Javadoc)
     * Method declared on IWorkbenchWizard.
     */
    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        super.init(workbench, currentSelection);
        setWindowTitle("New " + TITLE);
        setNeedsProgressMonitor(true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    public boolean canFinish() {
    	return mainPage.isPageComplete();
    }
    
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		mainPage.finish();
	}

	public boolean performFinish() {
		return mainPage.finish();
	}
	
    
//    /* (non-Javadoc)
//     * Method declared on IWizard.
//     */
//    public boolean performFinish() {
//        IFile file = mainPage.createNewFile();
//        if (file == null) {
//			return false;
//		}
//
//        selectAndReveal(file);
//
//        // Open editor on new file.
//        IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
//        try {
//            if (dw != null) {
//                IWorkbenchPage page = dw.getActivePage();
//                if (page != null) {
//                    IDE.openEditor(page, file, true);
//                }
//            }
//        } catch (PartInitException e) {
//            ErrorHelper.openError(dw.getShell(), FILE_CREATION_ERROR_MSG, 
//                    e.getMessage(), e);
//        }
//
//        return true;
//    }

    
}