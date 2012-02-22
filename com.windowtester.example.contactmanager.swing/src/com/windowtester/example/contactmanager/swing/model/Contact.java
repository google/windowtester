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
import java.util.TreeMap;

public class Contact
	implements IContact
{
	private String firstName;
	private String lastName;

	private String address;
	private String city;
	private String state;
	private String zip;

	private String homePh;
	private String officePh;
	private String mobilePh;
	private String email;
	private String street;

	public Contact() {

	}

	public Contact(String lName, String fName, String ph) {
		firstName = fName;
		lastName = lName;
		homePh = ph;

		Contacts.addContact(this);

	}

	public static Contact loadContact(String fname, String lname, String hphone) {

		Contact contact = new Contact(lname, fname, hphone);

		return contact;
	}

	public String getFirstName() {
		return firstName;

	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZip() {
		return zip;
	}

	public String getHomePh() {
		return homePh;
	}

	public String getOfficePh() {
		return officePh;
	}

	public String getMobilePh() {
		return mobilePh;
	}

	public String getEmail() {
		return email;
	}

	public void setFirstName(String fname) {
		firstName = fname;
	}

	public void setLastName(String lname) {
		lastName = lname;
	}

	public void setAddress(String addr, String cty, String st, String zp) {
		address = addr;
		city = cty;
		state = st;
		zip = zp;
	}

	public void setHomePh(String ph) {
		homePh = ph;
	}

	public void setOfficePh(String ph) {
		officePh = ph;
	}

	public void setMobilePh(String ph) {
		mobilePh = ph;
	}

	public void setEmail(String em) {
		email = em;
	}

	public String toString() {
		String name = "toString";

		// TODO: tostring

		// String fnameFirst =
		// prefs.getString(PreferenceConstants.CONTACTS_DISPLAY_BY__FIRST_NAME);
		// if (fnameFirst.equals("0"))
		// name = firstName + "," + lastName;
		// else name = lastName + "," + firstName;
		return name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

}
