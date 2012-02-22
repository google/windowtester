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

import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.windowtester.example.contactmanager.rcp.model.Contact;
import com.windowtester.example.contactmanager.rcp.model.ContactsManager;
import com.windowtester.example.contactmanager.rcp.swing.ContactEditor;


public class EmbeddedSwingFramePage extends WizardPage {

	
	private Display display;
	private ContactEditor cEditor;
	private Contact contact;
	
	
	protected EmbeddedSwingFramePage() {
		super("wizardPage");
		setTitle("New Contact");
		setDescription("Enter information for new contact\nThis page contains an embedded AWT frame");
		setPageComplete(false);
		display = Display.getCurrent();
	}

	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.EMBEDDED);
		composite.setLayout(new FillLayout());

		final Frame frame = SWT_AWT.new_Frame(composite);	
		contact = new Contact("", "", "");
		//final Panel panel = createPanel();
		final ContactEditor contactEditor = new ContactEditor(contact,false);
		cEditor = contactEditor;
		contactEditor.getLastNameText().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				display.asyncExec(new Runnable() {
			        public void run() {
			            setPageComplete(true);
			          }
			        });
			}
		});
		
	//	frame.add(panel);
		frame.add(contactEditor);
		frame.addWindowListener( new WindowAdapter() {
		    public void windowActivated( WindowEvent e ){
		         contactEditor.getFirstNameText().requestFocus();
		      }
		} );
		
		frame.pack();
		frame.setVisible(true);
	
		setControl(composite);
		
	}
	
	

	public void createContact(){
		Contact contact = new Contact(cEditor.getLastNameText().getText(),cEditor.getFirstNameText().getText(),cEditor.getHomeText().getText());
		contact.setAddress(cEditor.getStreetText().getText(),cEditor.getCityText().getText(),
						   cEditor.getStateText().getText(),cEditor.getZipText().getText());
		contact.setEmail(cEditor.getEmailText().getText());
		contact.setMobilePh(cEditor.getMobileText().getText());
		contact.setOfficePh(cEditor.getOfficeText().getText());
		ContactsManager mgr = ContactsManager.getManager();
		mgr.newContact(contact);
		
	}
	

}
