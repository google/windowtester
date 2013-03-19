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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextTestShell {

	private Text text;
	protected Shell shell;

	public Shell getShell() {
		return shell;
	}

	
	public boolean ALPHA;
	public boolean BETA;

	
	/**
	 * Open the window
	 */
	public void open() {
		shell = new Shell();
		createContents();
		shell.open();

		text = new Text(shell, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		final Menu menu_4 = new Menu(text);
		text.setMenu(menu_4);

		final MenuItem alphaMenuItem = new MenuItem(menu_4, SWT.NONE);
		alphaMenuItem.setText("alpha");
		alphaMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				toggleALPHA();
			}
		});
		
		final MenuItem betaMenuItem = new MenuItem(menu_4, SWT.NONE);
		betaMenuItem.setText("beta");
		betaMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				toggleBETA();
			}
		});
		
		
		
		shell.layout();
	}

	void createContents() {
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setSize(316, 67);
		shell.setText("Text Test");
	}
	public Text getText() {
		return text;
	}
	
	private void toggleALPHA() {
		ALPHA = !ALPHA;
	}
	
	private void toggleBETA() {
		BETA = !BETA;
	}
	
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TextTestShell window = new TextTestShell();
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
	
}
