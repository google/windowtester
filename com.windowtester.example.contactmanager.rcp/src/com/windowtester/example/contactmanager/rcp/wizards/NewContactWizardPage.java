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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.windowtester.example.contactmanager.rcp.ContactManagerRCPPlugin;
import com.windowtester.example.contactmanager.rcp.model.Contact;
import com.windowtester.example.contactmanager.rcp.model.ContactsManager;
import com.windowtester.example.contactmanager.rcp.preferences.PreferenceConstants;


public class NewContactWizardPage extends WizardPage
{
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
	private boolean fnameEntered = false;
	private boolean lnameEntered = false;
	
	public NewContactWizardPage() {
		super("wizardPage");
		setTitle("New Contact");
		setDescription("Enter information for new contact");
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
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
		initContents();
		setControl(container);
		
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
		fname.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				fnameEntered = true;
				setPageComplete(fnameEntered && lnameEntered);
			}
		});

		final Label lastNameLabel = new Label(nameGroup, SWT.NONE);
		lastNameLabel.setLayoutData(new GridData());
		lastNameLabel.setText("Last Name");

		lname = new Text(nameGroup, SWT.BORDER);
		final GridData gridData_1 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData_1.widthHint = 175;
		lname.setLayoutData(gridData_1);
		lname.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				lnameEntered = true;
				setPageComplete(fnameEntered && lnameEntered);
			}
		});
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
		
		final Label cityLabel = new Label(Address, SWT.NONE);
		cityLabel.setText("City");

		city = new Text(Address, SWT.BORDER);
		city.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		final Label stateLabel = new Label(Address, SWT.NONE);
		stateLabel.setLayoutData(new GridData());
		stateLabel.setText("State");

		state = new Text(Address, SWT.BORDER);
		final GridData gridData_3 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData_3.widthHint = 132;
		state.setLayoutData(gridData_3);
		
		final Label zipLabel = new Label(Address, SWT.NONE);
		zipLabel.setText("Zip");

		zip = new Text(Address, SWT.BORDER);
		zip.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
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
		
		final Label office = new Label(Phone, SWT.NONE);
		office.setLayoutData(new GridData());
		office.setText("Office");

		officePh = new Text(Phone, SWT.BORDER);
		final GridData gridData_3_1 = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridData_3_1.widthHint = 114;
		officePh.setLayoutData(gridData_3_1);
		
		final Label mobile = new Label(Phone, SWT.NONE);
		mobile.setText("Mobile");

		mobilePh = new Text(Phone, SWT.BORDER);
		final GridData gridData_6 = new GridData(GridData.FILL, GridData.END, false, true);
		gridData_6.widthHint = 105;
		mobilePh.setLayoutData(gridData_6);
		
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
	}
	
	private void initContents(){
		IPreferenceStore prefs = ContactManagerRCPPlugin
							.getDefault().getPreferenceStore();
		String areaCode = "(" +
			prefs.getString(PreferenceConstants.CONTACTS_DEFAULT_PHONE_AREA_CODE)
			+ ")";
		mobilePh.setText(areaCode);
		officePh.setText(areaCode);
		homePh.setText(areaCode);
	}
	
	public void createContact(){
		Contact contact = new Contact(lname.getText(),fname.getText(),homePh.getText());
		contact.setAddress(street.getText(),city.getText(),
						   state.getText(),zip.getText());
		contact.setEmail(email.getText());
		contact.setMobilePh(mobilePh.getText());
		contact.setOfficePh(officePh.getText());
		ContactsManager mgr = ContactsManager.getManager();
		mgr.newContact(contact);
		
	}
	
}
