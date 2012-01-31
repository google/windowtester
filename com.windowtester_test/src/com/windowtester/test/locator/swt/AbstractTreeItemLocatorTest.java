package com.windowtester.test.locator.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;

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
public abstract class AbstractTreeItemLocatorTest extends AbstractLocatorTest {

	private static final String SHELL_TITLE = "TreeTestShell";
	

	class TreeShell {
		
		private Shell shell;

		/**
		 * Open the window
		 */
		public void open() {
			shell = new Shell();
			shell.setText(SHELL_TITLE);
			createContents();
			shell.layout();
			shell.open();
		}

		private void createContents() {
			shell.setSize(380, 425);
			shell.setLayout(new GridLayout());
			Tree tree = new Tree(shell, SWT.NONE);
			tree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
			createTreeContents(tree);
		}

		public Shell getShell() {
			return shell;
		}
	}
	
	
	TreeShell window;
	
	
	@Override
	public void uiSetup() {
		window = new TreeShell();
		window.open();
		
		wait(new ShellShowingCondition(SHELL_TITLE));
	}
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}

	protected abstract void createTreeContents(Tree tree);
	


	
	
	
	
}
