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

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ComboTestShell extends TestCase {

	private Combo combo;
	protected Shell shell;

	public final static String[] COMBO_TEST_SHELL_ITEMS = {"one", "two", "three", "four", "five 5",
		"many many many many words", "Subtree OF", "tab\tconfusion", "tab	confusion 2",
		"=", "!="};
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ComboTestShell shell = new ComboTestShell();
			shell.open();
			final Display display = Display.getDefault();
			while (!shell.getShell().isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Shell getShell() {
		return shell;
	}

	/**
	 * Open the window
	 */
	public void open() {
		shell = new Shell();
		createContents();
		shell.open();

		combo = new Combo(shell, SWT.NONE);
		combo.setItems(COMBO_TEST_SHELL_ITEMS);
		combo.setData("newKey", null);
		final GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.widthHint = 146;
		combo.setLayoutData(gridData);
		shell.layout();
	}

	void createContents() {
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setSize(316, 67);
		shell.setText("Combo Test");
	}
	public Combo getCombo() {
		return combo;
	}

}
