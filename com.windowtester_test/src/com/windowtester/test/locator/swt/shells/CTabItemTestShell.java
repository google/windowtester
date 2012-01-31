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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CTabItemTestShell  {

	private CTabFolder tabFolder;
	protected Shell shell;

	
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CTabItemTestShell window = new CTabItemTestShell();
			window.open();
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

		tabFolder = new CTabFolder(shell, SWT.CLOSE);

		final CTabItem einsTabItem = new CTabItem(tabFolder, SWT.NONE);
		einsTabItem.setText("eins");

		final CTabItem zweiTabItem = new CTabItem(tabFolder, SWT.NONE);
		zweiTabItem.setText("zwei");

		final CTabItem dreiTabItem = new CTabItem(tabFolder, SWT.NONE);
		dreiTabItem.setText("drei");
		shell.layout();
	}
	
	
	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setSize(258, 150);
		shell.setText("CTabItem Test");
		
		
	}
	
	public Shell getShell() {
		return shell;
	}
	public CTabFolder getCTabFolder() {
		return tabFolder;
	}
	
	
}
