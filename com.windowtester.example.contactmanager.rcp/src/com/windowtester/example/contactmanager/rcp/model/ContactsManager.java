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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.windowtester.example.contactmanager.rcp.ContactManagerRCPPlugin;
import com.windowtester.example.contactmanager.rcp.ContactsViewContentProvider;


public class ContactsManager 
	implements IResourceChangeListener {
	
	private static final String TAG_CONTACTS = "Contacts";
	private static final String TAG_CONTACT = "Contact";
	private static final String TAG_FNAME = "Fname";
	private static final String TAG_LNAME = "Lname";
	private static final String TAG_HPHONE = "Hphone";
	private static final String TAG_OPHONE = "Ophone";
	private static final String TAG_MPHONE = "Mphone";
	private static final String TAG_STREET = "Street";
	private static final String TAG_CITY = "City";
	private static final String TAG_STATE = "State";
	private static final String TAG_ZIP = "Zip";
	private static final String TAG_EMAIL = "Email";
	
	private static ContactsManager manager;
	private Collection contacts;
	private List listeners = new ArrayList();
	
	private ContactsManager(){
		ResourcesPlugin
			.getWorkspace()
			.addResourceChangeListener(
				this,IResourceChangeEvent.POST_CHANGE);
	}
	
	///  IContact Accessors ///
	
	public static ContactsManager getManager(){
		if (manager == null)
			manager = new ContactsManager();
		return manager;
	}
	
	public IContact[] getContacts(){
		if (contacts == null)
			loadContacts();
		return (IContact[]) contacts.toArray(
				new IContact[contacts.size()]);
	}
	
	public void addContacts(IContact[] newContacts){
		if (contacts == null)
			loadContacts();
		if (contacts.addAll(Arrays.asList(newContacts)))
			fireContactsChanged(newContacts,IContact.NONE);
	}
	
	public void newContact(IContact newContact){
		if (contacts == null)
			loadContacts();
		if (contacts.add(newContact)){
			IContact[] newCts = {(IContact)newContact};
			fireContactsChanged(newCts,IContact.NONE);
		}
	}
	
	public void removeContacts(IContact[] oldContacts){
		if (contacts == null)
			loadContacts();
		if (contacts.removeAll(Arrays.asList(oldContacts)))
			fireContactsChanged(IContact.NONE,oldContacts);
	}
	
	private void loadContacts(){
		contacts = new HashSet(20);
		Reader reader = null;
		File file = getContactsFile();
		String path = file.getAbsolutePath();
		try {
			reader = new FileReader(file);
		}
		catch (FileNotFoundException e1) {
			try {
				URL entry = ContactManagerRCPPlugin.getDefault().getBundle().getEntry(file.getName());
				path = entry.toString();
				reader = new InputStreamReader(entry.openStream());
			}
			catch (IOException e) {
				ContactManagerRCPPlugin.logError("Failed to find default contacts", e);
				return;
			}
		}
		try {
			loadContacts(XMLMemento.createReadRoot(reader));
		}
		catch (Exception e){
			ContactManagerRCPPlugin.logError("Failed to load contacts from " + path, e);
		}
		finally {
			try {
				reader.close();
			}
			catch (IOException e) {
				ContactManagerRCPPlugin.logError("Failed to close reader", e);
			}
		}
	}

	private void loadContacts(XMLMemento memento){
		IMemento [] children =
			memento.getChildren(TAG_CONTACT);
		for (int i = 0; i < children.length; i++){
			Contact contact =
				Contact.loadContact(
					children[i].getString(TAG_FNAME),
					children[i].getString(TAG_LNAME),
					children[i].getString(TAG_HPHONE));
			contact.setOfficePh(children[i].getString(TAG_OPHONE));
			contact.setMobilePh(children[i].getString(TAG_MPHONE));
			contact.setAddress(
				children[i].getString(TAG_STREET),
				children[i].getString(TAG_CITY),
				children[i].getString(TAG_STATE),
				children[i].getString(TAG_ZIP));
			contact.setEmail(children[i].getString(TAG_EMAIL));
			if (contact != null)
				contacts.add(contact);
		}
	}
	

	//// ContactsManager Listener Methods /////
	public void addContactsManagerListener(
			ContactsViewContentProvider listener){
		
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeContactsManagerListener(
			ContactsViewContentProvider listener){
				listeners.remove(listener);
	}
	
	private void fireContactsChanged(
		IContact[] contactsAdded,
		IContact[] contactsRemoved){
		
		ContactsManagerEvent event
			= new ContactsManagerEvent(this,contactsAdded,contactsRemoved);
		for (Iterator iter = listeners.iterator(); iter.hasNext();)
			((ContactsViewContentProvider)iter.next()).contactsChanged(event);
	}

	public void saveContacts(){
		if (contacts == null)
			return;
		XMLMemento memento =
			XMLMemento.createWriteRoot(TAG_CONTACTS);
		saveContacts(memento);
		FileWriter writer = null;
		try {
			writer = new FileWriter(getContactsFile());
			memento.save(writer);
		}
		catch(IOException e){

		}
		finally {
			try {
				if (writer != null)
					writer.close();
			}
			catch (IOException e){

			}
		}
	}
	
	private void saveContacts(XMLMemento memento){
		Iterator iter = contacts.iterator();
		while (iter.hasNext()){
			Contact contact = (Contact)iter.next();
			IMemento child = memento.createChild(TAG_CONTACT);
			child.putString(TAG_FNAME,contact.getFirstName());
			child.putString(TAG_LNAME,contact.getLastName());
			child.putString(TAG_HPHONE,contact.getHomePh());
			child.putString(TAG_OPHONE,contact.getOfficePh());
			child.putString(TAG_MPHONE,contact.getMobilePh());
			child.putString(TAG_STREET,contact.getAddress());
			child.putString(TAG_CITY,contact.getCity());
			child.putString(TAG_STATE,contact.getState());
			child.putString(TAG_ZIP,contact.getZip());
			child.putString(TAG_EMAIL,contact.getEmail());
		}
	}
	
	private File getContactsFile(){
		return ContactManagerRCPPlugin
			.getDefault()
			.getStateLocation()
			.append("contacts.xml")
			.toFile();
	}

	public static void shutdown(){
		if (manager != null){
			ResourcesPlugin
				.getWorkspace()
				.removeResourceChangeListener(manager);
			manager = null;
		}
	}

	public void resourceChanged(IResourceChangeEvent event) {
		// TODO Auto-generated method stub
		
	}	
	
}
