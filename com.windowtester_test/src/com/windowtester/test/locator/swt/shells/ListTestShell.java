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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class ListTestShell {


	private List list;
	protected Shell shell;
	private Menu popUpMenu;
	
	public final static String[] LIST_TEST_SHELL_ITEMS = {"one", "two", "three", "four", "five", "six", "seven"};
	
	public Shell getShell() {
		return shell;
	}
	
	/**
	 * Open the window
	 */
	public void open() {
		shell = new Shell();
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		createContents();
		shell.open();

		list = new List(shell, SWT.BORDER | SWT.MULTI);
		list.setItems(LIST_TEST_SHELL_ITEMS);
		list.setLayoutData(new GridData(216, SWT.DEFAULT));
		
		popUpMenu = new Menu(shell,SWT.POP_UP);
		list.setMenu(popUpMenu);
		MenuItem menuItem = new MenuItem(popUpMenu,SWT.PUSH);
		menuItem.setText("Menu item 1");
		menuItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("menu item selected");					
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("menu item selected");				
			}
		});
		
		shell.layout();
		shell.pack();
	}

	void createContents() {
		shell.setSize(242, 128);
		shell.setText("List Test");
	}
	public List getList() {
		return list;
	}

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ListTestShell window = new ListTestShell();
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



	
}
