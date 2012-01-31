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
package com.windowtester.test.locator.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.LabeledLabelLocator;


public class LabeledLabelLocatorTest extends AbstractLocatorTest {

	private static final String SHELL_TITLE = "TestShell";

	
	class TestShell {
		
		private Shell shell;

		/**
		 * Open the window
		 */
		public void open() {
			shell = new Shell();
			shell.setText(SHELL_TITLE);
			createContents();
			//shell.pack();
			shell.layout();
			shell.open();
//			shell.layout();
		}

		private void createContents() {
			shell.setSize(380, 425);
			GridLayout layout = new GridLayout(1, false);
			shell.setLayout(layout);
			Composite composite = new Composite(shell, SWT.NONE);
			composite.setLayoutData(new GridData(400,400));
			composite.setLayout(new GridLayout(2, false));
			Label label = new Label(composite, SWT.NONE);
			label.setText("Name:");
			Label label2 = new Label(composite, SWT.NONE);
			label2.setText("Bart");
			Label label3 = new Label(composite, SWT.NONE);
			label3.setText("Type:");
			Label label4 = new Label(composite, SWT.NONE);
			label4.setText("Alpha");
			
			
		}

		public Shell getShell() {
			return shell;
		}
	}


	private TestShell window;
	
	
	@Override
	public void uiSetup() {
		window = new TestShell();
		window.open();
		
		wait(new ShellShowingCondition(SHELL_TITLE));
	}
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	
	public void testAssertLabeledLabelHasText() throws Exception {
		IUIContext ui = getUI();
		ui.assertThat(new LabeledLabelLocator("Name:").hasText("Bart"));
		ui.assertThat(new LabeledLabelLocator("Type:").hasText("Alpha"));
		
		
	}
	
}
