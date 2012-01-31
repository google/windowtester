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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class TabItemTestShell {

	private TabFolder tabFolder;
	protected Shell shell;


	
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TabItemTestShell window = new TabItemTestShell();
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

		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(new GridData(132, 54));

		final TabItem unoTabItem = new TabItem(tabFolder, SWT.NONE);
		unoTabItem.setText("uno");

		final TabItem doesTabItem = new TabItem(tabFolder, SWT.NONE);
		doesTabItem.setText("dos");

		final TabItem tresTabItem = new TabItem(tabFolder, SWT.NONE);
		tresTabItem.setText("tres");
		shell.layout();
	}
	
	
	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setSize(258, 150);
		shell.setText("TabItem Test");
		
		
	}
	
	public Shell getShell() {
		return shell;
	}
	public TabFolder getTabFolder() {
		return tabFolder;
	}
	
	
}
