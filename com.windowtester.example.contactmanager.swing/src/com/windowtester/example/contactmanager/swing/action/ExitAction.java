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

/**
 * Action to exit the application.
 *
 * @author Leman Reagan
 */
public class ExitAction	implements ActionListener
{
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}