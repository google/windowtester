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
import org.eclipse.jface.dialogs.MessageDialog;

import com.windowtester.example.contactmanager.rcp.View;




public class CopyContactAction extends Action
{
	private View view;

	public CopyContactAction(
		View view,String text){
		
		super(text);
		this.view = view;
	}
	
	public void run(){
		if(view != null) {	
			try {
				MessageDialog.openInformation(view.getSite().getShell(), "Action", "Executing copy Action");
			} catch (Exception e) {
				MessageDialog.openError(view.getSite().getShell(), "Error", "Error opening dialog" + e.getMessage());
			}
		}
		
	}
	
}
