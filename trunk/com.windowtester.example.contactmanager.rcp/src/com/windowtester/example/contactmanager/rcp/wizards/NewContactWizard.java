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

public class NewContactWizard extends Wizard
{
	private NewContactWizardPage contactPage;
	
	
	public NewContactWizard(){
		super();
	}
	
	public void addPages(){
		contactPage = new NewContactWizardPage();
		addPage(contactPage);
	}
	
	 public boolean canFinish() {
		 return (contactPage.isPageComplete());
	  }
	 
	public boolean performFinish() {
		contactPage.createContact();
		return true;
	}

}
