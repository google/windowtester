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
 
package com.windowtester.example.contactmanager.rcp.swing;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;



public class OpenDialogAction extends Action {

	
	
	public OpenDialogAction(IWorkbenchWindow window, String label) {
        setText(label);
//      The id is used to refer to the action in a menu or toolbar
		setId("swing.dialog");
       }
	
	public void run() {
		NewContactDialog dialog = new NewContactDialog();
		dialog.setVisible(true);
	}
	
}
