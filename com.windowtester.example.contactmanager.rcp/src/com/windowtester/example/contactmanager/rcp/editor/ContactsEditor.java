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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.windowtester.example.contactmanager.rcp.model.Contact;



public class ContactsEditor extends EditorPart
{
	public static final String ID = "com.windowtester.example.contactmanager.rcp.editor";
	
	private Text lname;
	private Text fname;
	private Text mobilePh;
	private Text officePh;
	private Text homePh;
	private Text zip;
	private Text state;
	private Text city;
	private Text street;
	private Text email;
	private boolean isDirty;
	private TextKeyListener textKeyListener = new TextKeyListener();
	private class TextKeyListener implements KeyListener {

		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void keyReleased(KeyEvent e) {
			// user has pressed key, so editor has to 
			// do a save. set the isdirty flag
			setDirty(true);
		}
		
	}
	

	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);

		final Composite composite = new Composite(container, SWT.NONE);
		composite.setRedraw(true);
		final GridData gridData_9 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData_9.widthHint = 360;
		composite.setLayoutData(gridData_9);
		composite.setLayout(new GridLayout());
		
		createNameGroup(composite);
		createAddressGroup(composite);
		createPhoneGroup(composite);
		createEmailGroup(composite);

		// fill in the values with the selected contact
		populateFields();
	}

	private void createNameGroup(Composite composite){
		final Group nameGroup = new Group(composite, SWT.NONE);
		nameGroup.setRedraw(true);
		nameGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		nameGroup.setText("Name");
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 2;
		nameGroup.setLayout(gridLayout_3);

		final Label firstNameLabel = new Label(nameGroup, SWT.NONE);
		firstNameLabel.setText("First Name");

		fname = new Text(nameGroup, SWT.BORDER);
		final GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 125;
		fname.setLayoutData(gridData);
		fname.addKeyListener(textKeyListener);

		final Label lastNameLabel = new Label(nameGroup, SWT.NONE);
		lastNameLabel.setLayoutData(new GridData());
		lastNameLabel.setText("Last Name");

		lname = new Text(nameGroup, SWT.BORDER);
		final GridData gridData_1 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData_1.widthHint = 175;
		lname.setLayoutData(gridData_1);
		lname.addKeyListener(textKeyListener);
	}
	
	private void createAddressGroup(Composite composite){
		final Group Address = new Group(composite, SWT.NONE);
		Address.setRedraw(true);
		Address.setText("Address");
		final GridData gridData_4 = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridData_4.widthHint = 337;
		Address.setLayoutData(gridData_4);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 4;
		Address.setLayout(gridLayout_1);

		final Label streetLabel = new Label(Address, SWT.NONE);
		streetLabel.setText("Street");

		street = new Text(Address, SWT.BORDER);
		final GridData gridData_2 = new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1);
		gridData_2.widthHint = 166;
		street.setLayoutData(gridData_2);
		street.addKeyListener(textKeyListener);
		
		final Label cityLabel = new Label(Address, SWT.NONE);
		cityLabel.setText("City");

		city = new Text(Address, SWT.BORDER);
		city.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		city.addKeyListener(textKeyListener);

		final Label stateLabel = new Label(Address, SWT.NONE);
		stateLabel.setLayoutData(new GridData());
		stateLabel.setText("State");

		state = new Text(Address, SWT.BORDER);
		final GridData gridData_3 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData_3.widthHint = 132;
		state.setLayoutData(gridData_3);
		state.addKeyListener(textKeyListener);
		
		final Label zipLabel = new Label(Address, SWT.NONE);
		zipLabel.setText("Zip");

		zip = new Text(Address, SWT.BORDER);
		zip.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		zip.addKeyListener(textKeyListener);
	}
	
	private void createPhoneGroup(Composite composite){
		final Group Phone = new Group(composite, SWT.NONE);
		Phone.setRedraw(true);
		Phone.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		Phone.setText("Phone");
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 4;
		Phone.setLayout(gridLayout_2);

		final Label home = new Label(Phone, SWT.NONE);
		home.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		home.setText("Home");

		homePh = new Text(Phone, SWT.BORDER);
		final GridData gridData_2_1 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData_2_1.widthHint = 112;
		homePh.setLayoutData(gridData_2_1);
		homePh.addKeyListener(textKeyListener);
		
		final Label office = new Label(Phone, SWT.NONE);
		office.setLayoutData(new GridData());
		office.setText("Office");

		officePh = new Text(Phone, SWT.BORDER);
		final GridData gridData_3_1 = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridData_3_1.widthHint = 114;
		officePh.setLayoutData(gridData_3_1);
		officePh.addKeyListener(textKeyListener);
		
		final Label mobile = new Label(Phone, SWT.NONE);
		mobile.setText("Mobile");

		mobilePh = new Text(Phone, SWT.BORDER);
		final GridData gridData_6 = new GridData(GridData.FILL, GridData.END, false, true);
		gridData_6.widthHint = 105;
		mobilePh.setLayoutData(gridData_6);
		mobilePh.addKeyListener(textKeyListener);
		
	}

	private void createEmailGroup(Composite composite){
		final Group emailGroup = new Group(composite, SWT.NONE);
		emailGroup.setRedraw(true);
		emailGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		emailGroup.setText("Email");
		final GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.numColumns = 2;
		emailGroup.setLayout(gridLayout_4);

		final Label emailLabel = new Label(emailGroup, SWT.NONE);
		emailLabel.setText("Email");

		email = new Text(emailGroup, SWT.BORDER);
		final GridData gridData_5 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData_5.widthHint = 268;
		email.setLayoutData(gridData_5);
		email.addKeyListener(textKeyListener);
	}
	
	public void setFocus() {
		fname.setFocus();
	}

	public void doSave(IProgressMonitor monitor) {
		MessageDialog.openInformation(this.getSite().getShell(), "Save",
		"Contact saved.");
		setDirty(false);
	}

	public void doSaveAs() {
		
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	public boolean isDirty() {
		return this.isDirty;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public void setDirty(boolean dirty){
		this.isDirty = dirty;
		/** force platform to re-check if editor is dirty or not
		 * to remove (*) from editors TAB
		 */
		firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
	}

	private void populateFields(){
		String value;
		ContactEditorInput contacts = (ContactEditorInput) getEditorInput();
		Contact contact = (Contact)contacts.getAdapter(Contact.class);
		
		this.fname.setText(contact.getFirstName());
		this.lname.setText(contact.getLastName());
		value = contact.getHomePh();
		this.homePh.setText(value != null ? value : "");
		value = contact.getOfficePh();
		this.officePh.setText(value != null ? value : "");
		value = contact.getMobilePh();
		this.mobilePh.setText(value != null ? value : "");
		value = contact.getAddress();
		this.street.setText(value != null ? value : "");
		value = contact.getCity();
		this.city.setText(value != null ? value : "");
		value = contact.getState();
		this.state.setText(value != null ? value : "");
		value = contact.getZip();
		this.zip.setText(value != null ? value : "");
		value = contact.getEmail();
		this.email.setText(value != null ? value : "");
		
		// set editors title to name of contact
		setPartName(contact.toString());
	}
	
}
