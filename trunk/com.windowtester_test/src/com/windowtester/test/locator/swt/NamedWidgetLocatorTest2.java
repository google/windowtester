package com.windowtester.test.locator.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;

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
public class NamedWidgetLocatorTest2 extends AbstractLocatorTest {

	private SwtApp shell;

	public static class SwtApp {

		private Button button;
		private Label label;
		private Text text;
		protected Shell shell;

		/**
		 * Launch the application
		 * @param args
		 */
		public static void main(String[] args) {
			try {
				SwtApp window = new SwtApp();
				window.open();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Open the window
		 */
		public void open() {
			createContents();
			shell.open();
			shell.layout();
		}

		/**
		 * Create contents of the window
		 */
		protected void createContents() {
			shell = new Shell();
			shell.setSize(500, 375);
			shell.setText(NamedWidgetLocatorTest2.class.getSimpleName() + "Shell");

			label = new Label(shell, SWT.NONE);
			label.setData("name", "samename");
			label.setText("Label");
			label.setBounds(36, 53, 25, 13);

			text = new Text(shell, SWT.BORDER);
			text.setData("name", "samename");
			text.setBounds(81, 50, 80, 25);

			button = new Button(shell, SWT.NONE);
			button.setData("name", "samename");
			button.setText("button");
			button.setBounds(82, 103, 44, 23);
			//
		}

		public void close() {
			shell.dispose();
		}
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.AbstractLocatorTest#uiSetup()
	 */
	@Override
	public void uiSetup() {
		shell = new SwtApp();
		shell.open();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.AbstractLocatorTest#uiTearDown()
	 */
	@Override
	public void uiTearDown() {
		shell.close();
	}
	
	/**
	 * Main test method.
	 */
	public void testNamedWidgetsUseClassInfo() throws Exception {
		IUIContext ui = getUI();
		ui.click(new NamedWidgetLocator(Label.class, "samename"));
		ui.click(new NamedWidgetLocator(Text.class, "samename"));
		ui.click(new NamedWidgetLocator(Button.class, "samename"));
	}

}