/*******************************************************************************
 * Copyright (c) 2012 Softvision GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Max Hohenegger (windowtester@hohenegger.eu). - initial implementation
 *******************************************************************************/
package com.windowtester.test.locator.swt.shells;

import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DynamicCompositeStacksTestShell {

	private class NinjaComposite extends Composite {

		public NinjaComposite(Composite parent) {
			super(parent, SWT.NONE);
			setLayout(new GridLayout());
		}
	}

	public static final String SHELL_LABEL = "Stacked Composite Test";
	protected Shell shell;

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DynamicCompositeStacksTestShell window = new DynamicCompositeStacksTestShell();
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
		gridLayout.numColumns = 3;

		shell.setLayout(gridLayout);
		shell.setSize(500, 500);
		shell.setText(SHELL_LABEL);

		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setLayout(new GridLayout());

		Group diversionGroup = new Group(composite, SWT.BORDER);
		diversionGroup.setText("Not me!");
		diversionGroup.setLayout(new GridLayout());
		if (new Random().nextBoolean()) {
			new Text(diversionGroup, SWT.NONE);
		}

		Group singleTextGroup = new Group(composite, SWT.BORDER);
		singleTextGroup.setText("Single");
		singleTextGroup.setLayout(new GridLayout());
		NinjaComposite ninjaComposite0 = new NinjaComposite(singleTextGroup);
		new Text(ninjaComposite0, SWT.NONE);

		Group doubleTextGroup = new Group(composite, SWT.BORDER);
		doubleTextGroup.setText("Double");
		doubleTextGroup.setLayout(new GridLayout());
		NinjaComposite ninjaComposite1 = new NinjaComposite(doubleTextGroup);
		new Text(ninjaComposite1, SWT.NONE);

		NinjaComposite ninjaComposite2 = new NinjaComposite(doubleTextGroup);
		NinjaComposite ninjaComposite3 = new NinjaComposite(ninjaComposite2);
		new Text(ninjaComposite3, SWT.NONE);

		Button button = new Button(shell, SWT.NONE);
		button.setText("button");

		Button button2 = new Button(shell, SWT.NONE);
		button2.setText("button");
		button2.setEnabled(false);
	}

	public Shell getShell() {
		return shell;
	}

}
