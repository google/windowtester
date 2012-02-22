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
 *******************************************************************************/

package com.windowtester.example.contactmanager.swing.model;

import java.util.ArrayList;

public class Contacts
{

	static ArrayList contacts = new ArrayList();
	
	public static void addContact(Contact contact) {
		contacts.add(contact);
	}

	public static ArrayList getContacts() {
		return contacts;
	}

}
