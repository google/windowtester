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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.windowtester.example.contactmanager.rcp.ContactManagerRCPPlugin;
import com.windowtester.example.contactmanager.rcp.preferences.PreferenceConstants;


public class Contact implements IContact
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
	
	private Color color;
	private static final Map colorCache = new HashMap();
	private static Color defaultColor;
	
	public Contact(){
		
	}
	
	public Contact(String lName, String fName,String ph){
		firstName = fName;
		lastName = lName;
		homePh = ph;	
	}
	
	public static Contact loadContact(
		String fname, String lname,
		String hphone){
	 
		Contact contact = new Contact(lname,fname,hphone);
		return contact;
}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public String getAddress(){
		return address;
	}
	
	public String getCity(){
		return city;
	}
	
	public String getState(){
		return state;
	}
	
	public String getZip(){
		return zip;
	}
	
	public String getHomePh(){
		return homePh;
	}
	
	public String getOfficePh(){
		return officePh;
	}
	
	public String getMobilePh(){
		return mobilePh;
	}
	
	public String getEmail(){
		return email;
	}
	public void setFirstName(String fname){
		firstName = fname;
	}
	
	public void setLastName(String lname){
		firstName = lname;
	}
	
	public void setAddress(
		String addr,String cty,String st, String zp){
		address = addr;
		city = cty;
		state = st;
		zip = zp;
	}
	
	public void setHomePh(String ph){
		homePh = ph;
	}
	
	public void setOfficePh(String ph){
		officePh = ph;
	}
	
	public void setMobilePh(String ph){
		mobilePh = ph;
	}
	
	public void setEmail(String em){
		email = em;
	}
	
	public String toString(){
		String name;
		IPreferenceStore prefs = ContactManagerRCPPlugin
							.getDefault().getPreferenceStore();
		String fnameFirst = 
			prefs.getString(PreferenceConstants.CONTACTS_DISPLAY_BY__FIRST_NAME);
		if (fnameFirst.equals("0"))
			name = firstName + "," + lastName;
		else name = lastName + "," + firstName;
		return name;
	}
	
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager()
			.getAdapter(this,adapter);
	}
	
	public Color getColor(){
		if (color == null)
			return getDefaultColor();
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public static Color getDefaultColor(){
		if (defaultColor == null)
			defaultColor = getColor(new RGB(0,0,0));
		return defaultColor;
	}
	
	public static void setDefaultColr(Color color){
		defaultColor = color;
	}
	
	public static Color getColor(RGB rgb){
		Color color = (Color) colorCache.get(rgb);
		if (color == null){
			Display display = Display.getCurrent();
			color = new Color(display,rgb);
			colorCache.put(rgb,color);
		}
		return color;
	}
	
	public static void disposeColors(){
		Iterator iter = colorCache.values().iterator();
		while(iter.hasNext())
			((Color)iter.next()).dispose();
		colorCache.clear();
	}

	public void setAddress(String address) {
		this.address = address;
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
