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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

public class ToolBarTestShell {

		protected Shell shell;
		
		public ToolItem itemA, itemB, itemC;
		
		ToolBarTestShell window;

		public Widget lastSelection;
		
		/**
		 * Launch the application
		 * @param args
		 */
		public static void main(String[] args) {
			try {
				ToolBarTestShell window = new ToolBarTestShell();
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

			final ToolBar toolBar = new ToolBar(shell, SWT.NONE);

			itemA = new ToolItem(toolBar, SWT.PUSH);
			itemA.setText("Item A");
			itemA.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					lastSelection = e.widget;
				}
				public void widgetDefaultSelected(SelectionEvent e) {	
				}
			});

			itemB = new ToolItem(toolBar, SWT.PUSH);
			itemB.setText("Item B");
			itemB.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					lastSelection = e.widget;
				}
				public void widgetDefaultSelected(SelectionEvent e) {	
				}
			});

			itemC = new ToolItem(toolBar, SWT.PUSH);
			itemC.setText("Item C");
			itemC.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					lastSelection = e.widget;
				}
				public void widgetDefaultSelected(SelectionEvent e) {	
				}
			});
			
			shell.layout();
		}
		
		
		/**
		 * Create contents of the window
		 */
		protected void createContents() {
			
			final GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 3;
			shell.setLayout(gridLayout);
			shell.setSize(171, 67);
			shell.setText("Tool Bar Test");

			
		}
		
		public Shell getShell() {
			return shell;
		}

		

	
}
