/*******************************************************************************
 *
 *   Copyright (c) 2012 Google, Inc.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *   
 *   Contributors:
 *   Google, Inc. - initial API and implementation
 *  
 *******************************************************************************/

package com.windowtester.example.contactmanager.rcp.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.windowtester.example.contactmanager.rcp.wizards.EmbeddedSwingFrameWizard;


public class OpenEmbeddedFrameAction extends Action {
	
	private final IWorkbenchWindow window;
	
	
	public OpenEmbeddedFrameAction(IWorkbenchWindow window, String label) {
		this.window = window;
        setText(label);
//      The id is used to refer to the action in a menu or toolbar
		setId("rcpContactsMngr.EmbeddedFrame");
        // Associate the action with a pre-defined command, to allow key bindings.
//		setActionDefinitionId("rcpContactsMngr.EmbeddedFrame");
	
	}
	
	
	public void run() {
		EmbeddedSwingFrameWizard wizard = new EmbeddedSwingFrameWizard();
		WizardDialog dialog =
			new WizardDialog(window.getShell(),wizard);
		dialog.open();
	}

}
