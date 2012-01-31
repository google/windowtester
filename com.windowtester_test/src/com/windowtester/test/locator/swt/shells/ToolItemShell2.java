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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.layout.GridData;

public class ToolItemShell2 extends org.eclipse.swt.widgets.Shell {

	public ToolItem item1;
	public ToolItem item2;
	public ToolItem itemA;
	public ToolItem itemB;
	public ToolBar toolBar1;
	public ToolBar toolBar2;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public ToolItemShell2(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new GridLayout(1, false));
		new Label(this, SWT.NONE);
		
		toolBar1 = new ToolBar(this, SWT.FLAT);
		toolBar1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		item1 = new ToolItem(toolBar1, SWT.NONE);
		item1.setText("Item 1");
		
		item2 = new ToolItem(toolBar1, SWT.NONE);
		item2.setText("Item 2");
		
		toolBar2 = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		itemA = new ToolItem(toolBar2, SWT.NONE);
		itemA.setText("Item A");
		
		itemB = new ToolItem(toolBar2, SWT.NONE);
		itemB.setText("Item B");
		new Label(this, SWT.NONE);
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(450, 300);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
