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

package com.windowtester.example.contactmanager.rcp;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.windowtester.example.contactmanager.rcp.model.ContactsManager;
import com.windowtester.example.contactmanager.rcp.model.ContactsManagerEvent;
import com.windowtester.example.contactmanager.rcp.model.ContactsManagerListener;


/**
 * The content provider class is responsible for providing objects to the
 * view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view,
 * or ignore it and always show the same content (like Task List, for
 * example).
 */
public class ContactsViewContentProvider 
	implements IStructuredContentProvider, ContactsManagerListener
 {
	
	private ContactsManager manager;
	private TableViewer viewer;
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		this.viewer = (TableViewer)v;
		if (manager != null)
			manager.removeContactsManagerListener(this);
		manager = (ContactsManager)newInput;
		if (manager != null)
			manager.addContactsManagerListener(this);
	
	}

	public void dispose() { 
	}

	public Object[] getElements(Object parent) {
		
		return manager.getContacts();
	}
	
	public void contactsChanged(
		final ContactsManagerEvent event){
		
		Display display = Display.getCurrent();
		if (display == null){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					contactsChanged(event);
				}
			});
		}
		
		viewer.getTable().setRedraw(false);
		try {

			viewer.remove(event.getContactsRemoved());
			viewer.add(event.getContactsAdded());
		}
		finally {
			viewer.getTable().setRedraw(true);
		}
//		viewer.refresh();
	}

	/***
 		Contact c1 = new Contact("Bourbon", "Matt", "9512384567");
		c1.setAddress("34 Washington pl");
		c1.setCity("Tualitin");
		c1.setZip("97224");
		c1.setMobilePh("9513458976");
		c1.setOfficePh("4085673444");
		c1.setEmail("matt.boubon@xyz.com");

		Contact c2 = new Contact("Bond", "James", "2120007007");
		c2.setAddress("67 Madison Ave");
		c2.setCity("New York");
		c2.setZip("23987");
		c2.setMobilePh("2100000007");
		c2.setOfficePh("2124567777");
		c2.setEmail("bond007@mi6.com");

		Contact c3 = new Contact("Mason", "Perry", "4158906754");
		c3.setAddress("32 Sunset Blvd");
		c3.setCity("Los Angles");
		c3.setZip("98765");
		c3.setMobilePh("7324568888");
		c3.setOfficePh("7560902121");
		c3.setEmail("perry_mason@sleuth.com");

		return new Contact[] {c1, c2, c3};
***/	
	
}