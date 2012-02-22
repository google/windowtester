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

package com.windowtester.example.contactmanager.rcp.swing;

import javax.swing.JDialog;

import org.eclipse.swt.widgets.Display;

public class NewContactDialog extends JDialog {
  
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NewContactDialog() {  
		super();
		setTitle("New Contact");
		Display display = Display.getCurrent();
		ContactEditor contactEditor = new ContactEditor(display,true);
	//	contactEditor.setOpaque(true);
		setContentPane(contactEditor);
		pack();
		setVisible(true);
  }

 
}
           