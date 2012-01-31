/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.test.locator.swt.shells;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

//import util.EventRecordingWatcher;

public class CComboTestShell  {

	private CCombo combo;
	protected Shell shell;
	
	public final static String[] CCOMBO_TEST_SHELL_ITEMS = {"ready", "steady",
		"go!", "one", "two", "three", "four", "five 5",
		"many many many many words", "Subtree OF", "tab\tconfusion",
		"tab	confusion 2", "=", "!="};
	
	public CCombo getCombo() {
		return combo;
	}


	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CComboTestShell window = new CComboTestShell();
			window.open();
			//new EventRecordingWatcher(window.getShell()).watch();
			
	
			final Display display = Display.getDefault();
			while (!window.getShell().isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Open the window
	 */
	public void open() {
		shell = new Shell();
		createContents();
		shell.open();

		combo = new CCombo(shell, SWT.NONE);
		combo.setItems(CCOMBO_TEST_SHELL_ITEMS);
		shell.layout();
	}
	
	
	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setSize(316, 67);
		shell.setText("CCombo Test");
		
		
	}
	
	public Shell getShell() {
		return shell;
	}
	
	
}
