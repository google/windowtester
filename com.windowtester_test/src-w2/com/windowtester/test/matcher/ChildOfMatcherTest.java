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

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class ChildOfMatcherTest extends AbstractMatcherTest {

	private Text text0;
	private Text text1;
	private Text text2;
	
	private Button button0;
	private Button button1;
	
	private ToolItem item0;
	private ToolItem item1;
	private ToolItem item2;
	private TestShell shell;
	
	public class TestShell extends Shell {

		

		public TestShell(Display display) {
			super(display, SWT.SHELL_TRIM);
			setLayout(new GridLayout(1, false));
			
			ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
			
			item0 = new ToolItem(toolBar, SWT.NONE);
			item0.setText("Item");
			
			item1 = new ToolItem(toolBar, SWT.NONE);
			item1.setText("Item");
			
			item2 = new ToolItem(toolBar, SWT.NONE);
			item2.setText("Item");
			
			Composite composite = new Composite(this, SWT.NONE);
			composite.setLayout(new GridLayout(1, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			
			text0 = new Text(composite, SWT.BORDER);
			
			text1 = new Text(composite, SWT.BORDER);
			
			text2 = new Text(composite, SWT.BORDER);
			
			button0 = new Button(composite, SWT.NONE);
			button0.setText("Button");
			
			button1 = new Button(composite, SWT.NONE);
			button1.setText("Button");
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
	
	
	public void testTextMatch0() throws Exception {
		assertMatches(text0, ofClass(Text.class).in(0, ofClass(Composite.class)));
	}
	
	public void testTextMatch1() throws Exception {
		assertMatches(text1, ofClass(Text.class).in(1, ofClass(Composite.class)));

	}
	
	public void testTextMatch2() throws Exception {
		assertMatches(text2, ofClass(Text.class).in(2, ofClass(Composite.class)));
	}
	
	public void testTextButton0() throws Exception {
		assertMatches(button0, ofClass(Button.class).in(0, ofClass(Composite.class)));
	}

	public void testTextButton1() throws Exception {
		assertMatches(button1, ofClass(Button.class).in(1, ofClass(Composite.class)));
	}
	
	
	public void testTextButtonWithText0() throws Exception {
		assertMatches(button0, ofClass(Button.class).and(withText("Button")).in(0, ofClass(Composite.class)));
	}
	
	public void testTextButtonWithText1() throws Exception {
		assertMatches(button1, ofClass(Button.class).and(withText("Button")).in(1, ofClass(Composite.class)));
	}
	
	
	
//	/**
//	 * Launch the application.
//	 * @param args
//	 */
//	public static void main(String args[]) {
//		try {
//			Display display = Display.getDefault();
//			TestShell shell = new TestShell(display);
//			shell.open();
//			shell.layout();
//			while (!shell.isDisposed()) {
//				if (!display.readAndDispatch()) {
//					display.sleep();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
}
