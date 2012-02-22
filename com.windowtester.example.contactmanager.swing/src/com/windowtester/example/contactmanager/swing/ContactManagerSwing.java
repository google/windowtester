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
 
package com.windowtester.example.contactmanager.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.Security;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.windowtester.example.contactmanager.swing.action.CreateContactAction;
import com.windowtester.example.contactmanager.swing.action.ExitAction;
import com.windowtester.example.contactmanager.swing.action.ToSystemTrayAction;
import com.windowtester.example.contactmanager.swing.editor.ContactEditor;
import com.windowtester.example.contactmanager.swing.model.Contact;
import com.windowtester.example.contactmanager.swing.model.Contacts;


/**
 * Contact Manager main application entry point... Singleton.
 * <p>
 * 
 * @author Leman Reagan
 */
public class ContactManagerSwing extends JPanel
{
	private static final long serialVersionUID = -3430907742358814207L;

	/**
	 * Singleton
	 */
	private static ContactManagerSwing instance;

	/**
	 * Answer the single instance of the Contact Manager
	 * 
	 * @return the instance (not <code>null</code>)
	 */
	public static ContactManagerSwing getInstance() {
		if (instance == null)
			instance = new ContactManagerSwing();
		return instance;
	}

	/**
	 * Main application entry point
	 * 
	 * @param args the launch arguments
	 */
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				// Create and set up the window.
				JFrame frame = new JFrame("Contact Manager");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				//frame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
				
				JMenuBar menuBar = getInstance().createMenuBar();
				frame.setJMenuBar(menuBar);
			
				// Create and set up the content pane.
				ContactManagerSwing demo = getInstance();
				demo.setOpaque(true);
				frame.setContentPane(demo);
				// Display the window.
				frame.pack();
				Security.addProvider(new com.sun.crypto.provider.SunJCE());
				frame.setVisible(true);
				System.out.println(System.getProperty("java.class.path"));
			}
		});
	}

	/**
	 * The list of contact that appears on the left side of the contact manager window
	 */
	private final JList list = new JList();

	/**
	 * Panel appearing on the right side of the contact manager window in which the
	 * contact "editors" appear showing detailed information for each selected contact.
	 */
	private final JTabbedPane tabbedPane = new JTabbedPane();

	/**
	 * Construct a new initialized instance
	 */
	private ContactManagerSwing() {
		super();
		setLayout(new GridBagLayout());

		final JSplitPane splitPane = new JSplitPane();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.ipady = 335;
		gridBagConstraints.ipadx = 300;
		add(splitPane, gridBagConstraints);

		splitPane.setRightComponent(tabbedPane);

		final JPanel listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout());
		listPanel.setPreferredSize(new Dimension(160, 0));
		splitPane.setLeftComponent(listPanel);
		
		listPanel.add(list);

		loadContacts();
		refreshList();

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					openContactEditor();
			}
		});
		list.setPreferredSize(new Dimension(150, 330));
	}

	private JMenuBar createMenuBar() {

		JMenuItem newContactItem = new JMenuItem("New Contact");
		JMenuItem exitItem = new JMenuItem("Exit");
		JMenuItem iconizeItem = new JMenuItem("Send to System Tray");

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		fileMenu.add(newContactItem);
		fileMenu.add(iconizeItem);
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);

		exitItem.addActionListener(new ExitAction());
		newContactItem.addActionListener(new CreateContactAction());
		iconizeItem.addActionListener(new ToSystemTrayAction());
		
		return menuBar;

	}

	/**
	 * Initialize the contact database with a fixed set of contacts
	 */
	private void loadContacts() {
		Contact.loadContact("James", "Bond", "(888)-007-0000");
		Contact.loadContact("Perry", "Mason", "(424)-442-2444");
		Contact.loadContact("Sam", "Little", "(112)-112-1122");
	}

	/**
	 * Refresh the list of contacts
	 */
	public void refreshList() {
		ArrayList arrayList = Contacts.getContacts();
		list.removeAll();

		String[] listData = new String[arrayList.size()];
		for (int i = 0; i < arrayList.size(); i++) {
			Contact contact = (Contact) arrayList.get(i);
			listData[i] = contact.getFirstName() + "," + contact.getLastName();
		}
		list.setListData(listData);
	}

	/**
	 * Open an "editor" (really just a JPanel) displaying information for the currently
	 * selected contact
	 */
	private void openContactEditor() {
		String contactName = list.getSelectedValue().toString();

		// Should look for panel already showing the contact's information
		// before opening a new panel

		JPanel contactEditor = new ContactEditor((Contact) Contacts.getContacts().get(list.getSelectedIndex()));
		TabCloseListener closeListener = new TabCloseListener(tabbedPane);
		tabbedPane.addTab(contactName, closeListener, contactEditor, null);
		tabbedPane.setSelectedComponent(contactEditor);
	}
}
