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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.eclipse.ui.usage.ProfiledAction;

/**
 * Action for creating a new target file resource within the currently
 * selected folder or project.
 * <p>
 * Based on {@link org.eclipse.ui.actions.CreateFileAction} and modified.
 */
public class CreateTargetFileAction extends ProfiledAction implements IObjectActionDelegate  {

    private static final String TITLE = "New Recording Target";

	private static final String TOOL_TIP = "Create a new WindowTester Recording Target";

    
	/**
     * The id of this action.
     */
    public static final String ID = UiPlugin.PLUGIN_ID + ".CreateTargetFileAction";//$NON-NLS-1$


    /**
     * The shell in which to show any dialogs.
     */
    protected IShellProvider shellProvider;

	private IStructuredSelection selection;
   
    public CreateTargetFileAction() {
    	this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	}
    
    /**
     * Creates a new action for creating a file resource.
     *
     * @param shell the shell for any dialogs
     */
    public CreateTargetFileAction(final Shell shell) {
        super(TITLE);
        Assert.isNotNull(shell);
        shellProvider = new IShellProvider(){
        	public Shell getShell(){
        		return shell;
        	}
        };
        initAction();
    }

    /**
     * Creates a new action for creating a file resource.
     * 
     * @param provider the shell for any dialogs
     * 
     */
    public CreateTargetFileAction(IShellProvider provider){
    	super(TOOL_TIP);
    	Assert.isNotNull(provider);
    	shellProvider = provider;
    	initAction();
    }
    /**
     * Initializes for the constructor.
     */
    private void initAction(){
    	setToolTipText(TOOL_TIP);
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
        setId(ID);
//        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
//				IIDEHelpContextIds.CREATE_FILE_ACTION);
    }
    /**
     * The <code>CreateTargetFileAction</code> implementation of this
     * <code>IAction</code> method opens a <code>BasicNewFileResourceWizard</code>
     * in a wizard dialog under the shell passed to the constructor
     * (non-Javadoc)
     * @see com.windowtester.eclipse.ui.usage.ProfiledAction#doRun()
     */
    public void doRun() {
    	NewTargetFileWizard wizard = new NewTargetFileWizard();
        wizard.init(PlatformUI.getWorkbench(), selection);
        
        wizard.setNeedsProgressMonitor(true);
        WizardDialog dialog = new WizardDialog(shellProvider.getShell(), wizard);
        dialog.create();
//        dialog.getShell().setText(TITLE);
//        PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(),
//                IIDEHelpContextIds.NEW_FILE_WIZARD);
        dialog.open();
    }


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.usage.ProfiledAction#doRun()
	 */
	public void doRun(IAction action) {
		run();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection)
			this.selection = (IStructuredSelection)selection;
	}
}
