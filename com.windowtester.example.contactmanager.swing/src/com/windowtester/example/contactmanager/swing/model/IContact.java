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



public interface IContact
{
	String getFirstName();
	String getLastName();
	String getAddress();
	String getCity();
	String getState();
	String getZip();
	String getHomePh();
	
	void setFirstName(String fname);
	void setLastName(String lname);
	void setAddress(String addr,String city,String state,String zip);
	void setHomePh(String ph);
	
	//static IContact[] NONE = new IContact[] {};
}
