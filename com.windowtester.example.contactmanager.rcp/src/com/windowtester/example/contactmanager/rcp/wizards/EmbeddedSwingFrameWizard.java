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
 
package com.windowtester.example.contactmanager.rcp.wizards;

import org.eclipse.jface.wizard.Wizard;

public class EmbeddedSwingFrameWizard extends Wizard {

	private EmbeddedSwingFramePage page;
	
	public EmbeddedSwingFrameWizard(){
		super();
	}
	
	public void addPages(){
		page = new EmbeddedSwingFramePage();
		addPage(page);
	}
	
	 public boolean canFinish() {
		 return (page.isPageComplete());
	//	 return true;
	  }
	
	public boolean performFinish() {
		page.createContact();
		return true;
	}

}
