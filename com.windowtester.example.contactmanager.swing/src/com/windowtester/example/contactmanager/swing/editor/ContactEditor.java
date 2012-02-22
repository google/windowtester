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
 
package com.windowtester.example.contactmanager.swing.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.windowtester.example.contactmanager.swing.action.NewContactCancelAction;
import com.windowtester.example.contactmanager.swing.action.NewContactSaveAction;
import com.windowtester.example.contactmanager.swing.model.Contact;


/**
 * The "editor" used to display and modify contact information. This is used in both the
 * main contact editor window and in the prompt for new contact dialog.
 * 
 * @author Leman Reagan
 */
public class ContactEditor extends JPanel
{
	private static final long serialVersionUID = -7563520452504789174L;
	
	private JTextField emailText;
	private JTextField mobileText;
	private JTextField officeText;
	private JTextField homeText;
	private JTextField zipText;
	private JTextField stateText;
	private JTextField cityText;
	private JTextField streetText;
	private JTextField lastNameText;
	private JTextField firstNameText;
	public Contact contact;

	public ContactEditor(Contact contact) {

		super();
		boolean newContact = false;
		this.contact = contact;
		createPanel(newContact);

		emailText.setText(contact.getEmail());
		mobileText.setText(contact.getMobilePh());
		officeText.setText(contact.getOfficePh());
		homeText.setText(contact.getHomePh());
		zipText.setText(contact.getZip());
		stateText.setText(contact.getState());
		cityText.setText(contact.getCity());
		streetText.setText(contact.getStreet());
		lastNameText.setText(contact.getLastName());
		firstNameText.setText(contact.getFirstName());

	}

	public ContactEditor() {
		super();
		boolean newContact = true;
		createPanel(newContact);
		this.contact = new Contact("", "", "");

	}

	private void createPanel(boolean newContact) {

		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setLayout(new GridBagLayout());

		final JLabel spacerRightTop = new JLabel();
		final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.ipady = 5;
		gridBagConstraints_24.ipadx = 5;
		gridBagConstraints_24.gridy = 0;
		gridBagConstraints_24.gridx = 10;
		add(spacerRightTop, gridBagConstraints_24);

		final JLabel firstNameLabel = new JLabel();
		firstNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		firstNameLabel.setText("First Name: ");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 10;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		add(firstNameLabel, gridBagConstraints);

		firstNameText = new JTextField();
		firstNameText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setFirstName(jtf.getText());
			}
		});

		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridwidth = 4;
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_1.ipadx = 100;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 6;
		add(firstNameText, gridBagConstraints_1);

		final JLabel lastNameLabel = new JLabel();
		lastNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lastNameLabel.setText("Last Name: ");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_2.ipadx = 10;
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 0;
		add(lastNameLabel, gridBagConstraints_2);

		lastNameText = new JTextField();
		lastNameText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setLastName(jtf.getText());
			}
		});
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridwidth = 4;
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_3.ipadx = 100;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 6;
		add(lastNameText, gridBagConstraints_3);

		final JSeparator separator = new JSeparator();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(10, 0, 0, 0);
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.ipady = 10;
		gridBagConstraints_4.ipadx = 10;
		gridBagConstraints_4.gridy = 3;
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridheight = 1;
		gridBagConstraints_4.gridwidth = 10;
		add(separator, gridBagConstraints_4);

		final JLabel streetLabel = new JLabel();
		streetLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		streetLabel.setText("Street: ");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_5.ipadx = 10;
		gridBagConstraints_5.gridy = 4;
		gridBagConstraints_5.gridx = 0;
		add(streetLabel, gridBagConstraints_5);

		streetText = new JTextField();
		streetText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setStreet(jtf.getText());
			}
		});
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.gridwidth = 4;
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.weightx = 1.0;
		gridBagConstraints_6.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_6.ipadx = 100;
		gridBagConstraints_6.gridy = 4;
		gridBagConstraints_6.gridx = 6;
		add(streetText, gridBagConstraints_6);

		final JLabel cityLabel = new JLabel();
		cityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		cityLabel.setText("City: ");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.ipadx = 10;
		gridBagConstraints_7.gridy = 5;
		gridBagConstraints_7.gridx = 0;
		add(cityLabel, gridBagConstraints_7);

		cityText = new JTextField();
		cityText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setCity(jtf.getText());
			}
		});
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.gridwidth = 4;
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.weightx = 1.0;
		gridBagConstraints_8.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_8.ipadx = 100;
		gridBagConstraints_8.gridy = 5;
		gridBagConstraints_8.gridx = 6;
		add(cityText, gridBagConstraints_8);

		final JLabel stateLabel = new JLabel();
		stateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		stateLabel.setText("State: ");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.ipadx = 10;
		gridBagConstraints_10.gridy = 6;
		gridBagConstraints_10.gridx = 0;
		add(stateLabel, gridBagConstraints_10);

		stateText = new JTextField();
		stateText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setState(jtf.getText());
			}
		});
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_11.weightx = 1.0;
		gridBagConstraints_11.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_11.ipadx = 100;
		gridBagConstraints_11.gridy = 6;
		gridBagConstraints_11.gridx = 6;
		add(stateText, gridBagConstraints_11);

		final JLabel zipLabel = new JLabel();
		zipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		zipLabel.setText("Zip: ");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.ipadx = 15;
		gridBagConstraints_12.gridy = 6;
		gridBagConstraints_12.gridx = 8;
		add(zipLabel, gridBagConstraints_12);

		zipText = new JTextField();
		zipText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setZip(jtf.getText());
			}
		});
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_9.ipadx = 55;
		gridBagConstraints_9.gridy = 6;
		gridBagConstraints_9.gridx = 9;
		add(zipText, gridBagConstraints_9);

		final JSeparator separator_1 = new JSeparator();
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.insets = new Insets(15, 0, 0, 0);
		gridBagConstraints_13.gridwidth = 10;
		gridBagConstraints_13.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_13.ipady = 10;
		gridBagConstraints_13.ipadx = 10;
		gridBagConstraints_13.gridy = 7;
		gridBagConstraints_13.gridx = 0;
		add(separator_1, gridBagConstraints_13);

		final JLabel homeLabel = new JLabel();
		homeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		homeLabel.setText("Home: ");
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_14.ipadx = 10;
		gridBagConstraints_14.gridy = 8;
		gridBagConstraints_14.gridx = 0;
		add(homeLabel, gridBagConstraints_14);

		homeText = new JTextField();
		homeText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setHomePh(jtf.getText());
			}
		});
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_15.weightx = 1.0;
		gridBagConstraints_15.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_15.ipadx = 100;
		gridBagConstraints_15.gridy = 8;
		gridBagConstraints_15.gridx = 6;
		add(homeText, gridBagConstraints_15);

		final JLabel officeLabel = new JLabel();
		officeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.ipady = 1;
		gridBagConstraints_16.ipadx = 5;
		gridBagConstraints_16.gridy = 8;
		gridBagConstraints_16.gridx = 8;
		add(officeLabel, gridBagConstraints_16);
		officeLabel.setText("Office: ");

		officeText = new JTextField();
		officeText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setOfficePh(jtf.getText());
			}
		});
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_17.ipadx = 65;
		gridBagConstraints_17.gridy = 8;
		gridBagConstraints_17.gridx = 9;
		add(officeText, gridBagConstraints_17);

		final JLabel mobileLabel = new JLabel();
		mobileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		mobileLabel.setText("Mobile: ");
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_18.ipadx = 10;
		gridBagConstraints_18.gridy = 9;
		gridBagConstraints_18.gridx = 0;
		add(mobileLabel, gridBagConstraints_18);

		mobileText = new JTextField();
		mobileText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setMobilePh(jtf.getText());
			}
		});
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.gridwidth = 4;
		gridBagConstraints_19.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_19.weightx = 1.0;
		gridBagConstraints_19.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_19.ipadx = 100;
		gridBagConstraints_19.gridy = 9;
		gridBagConstraints_19.gridx = 6;
		add(mobileText, gridBagConstraints_19);

		final JLabel emailLabel = new JLabel();
		emailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		emailLabel.setText("Email: ");
		final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_21.ipadx = 10;
		gridBagConstraints_21.gridy = 10;
		gridBagConstraints_21.gridx = 0;
		add(emailLabel, gridBagConstraints_21);

		emailText = new JTextField();
		emailText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				JTextField jtf = (JTextField) e.getSource();
				contact.setEmail(jtf.getText());
			}
		});
		final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.gridwidth = 4;
		gridBagConstraints_22.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_22.weightx = 1.0;
		gridBagConstraints_22.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_22.ipadx = 100;
		gridBagConstraints_22.gridy = 10;
		gridBagConstraints_22.gridx = 6;
		add(emailText, gridBagConstraints_22);

		if (newContact) {

			final JLabel spacerButton = new JLabel();
			final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
			gridBagConstraints_20.ipady = 5;
			gridBagConstraints_20.gridy = 11;
			gridBagConstraints_20.gridx = 9;
			add(spacerButton, gridBagConstraints_20);

			final JButton finishButton = new JButton();
			finishButton.setHorizontalAlignment(SwingConstants.RIGHT);
			finishButton.addActionListener(new NewContactSaveAction());
			finishButton.setText("Finish");
			final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
			gridBagConstraints_23.anchor = GridBagConstraints.EAST;
			gridBagConstraints_23.gridy = 12;
			gridBagConstraints_23.gridx = 6;
			add(finishButton, gridBagConstraints_23);

			final JButton cancelButton = new JButton();
			cancelButton.addActionListener(new NewContactCancelAction());
			cancelButton.setText("Cancel");
			final GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
			gridBagConstraints_25.gridy = 12;
			gridBagConstraints_25.gridx = 9;
			add(cancelButton, gridBagConstraints_25);
		}
		final JLabel spacerBottom = new JLabel();
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.ipady = 5;
		gridBagConstraints_20.weighty = 1.0;
		gridBagConstraints_20.gridy = 13;
		gridBagConstraints_20.gridx = 0;
		add(spacerBottom, gridBagConstraints_20);

	}

	public Contact getContact() {
		return contact;
	}

}
