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

import javax.swing.JButton;

import com.windowtester.example.contactmanager.swing.ContactManagerSwing;
import com.windowtester.example.contactmanager.swing.editor.ContactEditor;


/**
 * Action to finish the prompt for new contact process
 * @author Leman Reagan
 */
public class NewContactSaveAction
	implements ActionListener
{
	public void actionPerformed(ActionEvent e) {
		JButton jButton = (JButton) e.getSource();
		ContactEditor ce = (ContactEditor) jButton.getParent();
		ContactManagerSwing.getInstance().refreshList();
		ce.getParent().getParent().getParent().setVisible(false);
	}
}