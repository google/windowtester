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
 
package com.windowtester.example.contactmanager.rcp.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.windowtester.example.contactmanager.rcp.model.Contact;



public class ContactEditorInput
	implements IEditorInput
{
	private Contact contact;
	
	public ContactEditorInput(Contact c){
		contact = c;
	}
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return("Contacts");
	}

	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipText() {
		return contact.toString();
	}

	public Object getAdapter(Class adapter) {
		if (adapter == Contact.class) 
			return this.contact;	
		else return null;
	}

}
