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

package com.windowtester.example.contactmanager.rcp.model;

import java.util.EventObject;


public class ContactsManagerEvent extends EventObject
{
    private static final long serialVersionUID = 5516075349620653480L;
	private final IContact[] added;
	private final IContact[] removed;

	   public ContactsManagerEvent(
	      ContactsManager source,
	      IContact[] contactsAdded,
	      IContact[] contactsRemoved) {
	         
	      super(source);
	      added = contactsAdded;
	      removed = contactsRemoved;
	   }
	   
	   public IContact[] getContactsAdded() {
	      return added;
	   }
	   
	   public IContact[] getContactsRemoved() {
	      return removed;
	   }

}
