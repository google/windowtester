/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *  Frederic Gurr - added checkboxes to table, added main method
 *******************************************************************************/
package com.windowtester.test.locator.swt.shells;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class TableTestShell {

	protected Shell shell;
	
	/**
	 * Open the window
	 */
	public void open() {
		shell = new Shell();
		createContents();
		shell.setSize (250, 250);
		shell.open ();
		shell.layout();
	}

	private void createContents() {
		Table table = new Table (shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		for (int i=0; i<12; i++) {
			TableItem item = new TableItem (table, 0);
			item.setText ("Item " + i);
		}
		table.setSize (100, 100);
		
		Table checkedTable = new Table (shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.CHECK);
		for (int i=0; i<12; i++) {
			TableItem item = new TableItem (checkedTable, 0);
			item.setText ("CheckedItem " + i);
		}
		checkedTable.setSize (100, 100);
		checkedTable.setLocation(130, 0);
	}

	public Shell getShell() {
		return shell;
	}
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
	 		TableTestShell window = new TableTestShell();
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
