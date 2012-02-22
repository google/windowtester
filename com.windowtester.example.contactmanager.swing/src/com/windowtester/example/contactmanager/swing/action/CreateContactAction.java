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
 
package com.windowtester.example.contactmanager.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import com.windowtester.example.contactmanager.swing.editor.ContactEditor;


/**
 * Action to prompt the user for new contact information and then add that contact to the
 * manager.
 * <p>
 * 
 * @author Leman Reagan
 */
public class CreateContactAction
	implements ActionListener
{
	public void actionPerformed(ActionEvent e) {
		JDialog jDialog = new JDialog();
		jDialog.setTitle("New Contact");

		ContactEditor contactEditor = new ContactEditor();
		contactEditor.setOpaque(true);
		jDialog.setContentPane(contactEditor);

		jDialog.pack();
		jDialog.setVisible(true);
	}
}
