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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ButtonTestShell  {

	public static final String SHELL_LABEL = "Button Test";
	private Button toggleButton;
	private Button radioButton;
	private Button checkButton;
	private Button button;
	protected Shell shell;
	
	private boolean buttonClicked;
	private boolean buttonChecked;
	private boolean buttonRadioed;
	private boolean buttonToggled;

	
	
	public boolean getButtonClicked() {
		return buttonClicked;
	}

	public boolean getButtonRadioed() {
		return buttonRadioed;
	}
	
	public boolean getButtonChecked() {
		return buttonChecked;
	}
	
	public boolean getButtonToggled() {
		return buttonToggled;
	}
	
	
	
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ButtonTestShell window = new ButtonTestShell();
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
		shell.layout();
	}
	
	
	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		shell.setLayout(gridLayout);
		shell.setSize(400, 67);
		shell.setText(SHELL_LABEL);
		
		button = new Button(shell, SWT.NONE);
		button.setText("button");
		button.setData("name", "b1");
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				buttonClicked = true;
			}
			public void widgetDefaultSelected(SelectionEvent e) {	
			}
		});

		checkButton = new Button(shell, SWT.CHECK);
		checkButton.setText("check button");
		checkButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				buttonChecked = true;
			}
			public void widgetDefaultSelected(SelectionEvent e) {	
			}
		});

		
		
		radioButton = new Button(shell, SWT.RADIO);
		radioButton.setText("radio button");
		radioButton.setData("name", "rb1");
		radioButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				buttonRadioed = true;
			}
			public void widgetDefaultSelected(SelectionEvent e) {	
			}
		});

		toggleButton = new Button(shell, SWT.TOGGLE);
		toggleButton.setText("toggle button");
		toggleButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				buttonToggled = true;
			}
			public void widgetDefaultSelected(SelectionEvent e) {	
			}
		});
		
	}
	
	public Shell getShell() {
		return shell;
	}

	
	public Button getButton() {
		return button;
	}
	public Button getCheckButton() {
		return checkButton;
	}
	public Button getRadioButton() {
		return radioButton;
	}
	public Button getToggleButton() {
		return toggleButton;
	}
	
	
}
