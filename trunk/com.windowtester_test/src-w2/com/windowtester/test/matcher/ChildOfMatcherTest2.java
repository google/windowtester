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
package com.windowtester.test.matcher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

public class ChildOfMatcherTest2 extends AbstractMatcherTest {

	public class TestShell extends Shell {
		private Text text;

//		/**
//		 * Launch the application.
//		 * @param args
//		 */
//		public static void main(String args[]) {
//			try {
//				Display display = Display.getDefault();
//				TootlItemShell2 shell = new TootlItemShell2(display);
//				shell.open();
//				shell.layout();
//				while (!shell.isDisposed()) {
//					if (!display.readAndDispatch()) {
//						display.sleep();
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

		/**
		 * Create the shell.
		 * @param display
		 */
		public TestShell(Display display) {
			super(display, SWT.SHELL_TRIM);
			setLayout(new GridLayout(1, false));
			
			Composite composite = new Composite(this, SWT.NONE);
			composite.setLayout(new GridLayout(1, false));
			composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
			
			Group grpProject = new Group(composite, SWT.NONE);
			grpProject.setText("&Project:");
			grpProject.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
			
			text = new Text(grpProject, SWT.BORDER);
			text.setBounds(10, 44, 78, 30);
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

	private TestShell shell;
	
	@Override
	public void uiSetup() {
		shell = new TestShell(Display.getDefault());
		shell.open();
		shell.layout();
	}
	
	@Override
	public void uiTearDown() {
		shell.dispose();
	}
	
	public void testFindGroupWithExactText() throws Exception {
		getUI().find(new SWTWidgetLocator(Group.class, "Project:"));
	}
	
	public void testFindGroupWithTrimmedText() throws Exception {
		getUI().find(new SWTWidgetLocator(Group.class, "&Project:"));
	}
	
	public void testClickText() throws Exception {
		getUI().click(new SWTWidgetLocator(Text.class, new SWTWidgetLocator(Group.class, "Project:")));	
	}
	
}
