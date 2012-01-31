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
package com.windowtester.test.eclipse.condition;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.EclipseUtil;
import com.windowtester.test.eclipse.helpers.JavaProjectHelper;

// TODO fix this test for 3.3
/**
 * @deprecated pending further investigation https://fogbugz.instantiations.com/default.php?43778
 *
 */
public class ShellMonitorSmokeTest extends BaseTest
{


	private final String TEST_PROJECT = getClass().getName() + "Project";
	private static String SHELL_TITLE = "Confirm Project Delete";
	static {
		if (EclipseUtil.isAtLeastVersion_34())
			SHELL_TITLE = "Delete Resources";
	}
	
	private static String CANCEL_BUTTON_TEXT = "No";
	static {
		if (EclipseUtil.isAtLeastVersion_34())
			CANCEL_BUTTON_TEXT = "Cancel";
	}
	

	
	/* (non-Javadoc)
	 * @see com.windowtester.test.eclipse.BaseTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUIContext ui = getUI();
		JavaProjectHelper.createJavaProject(ui, TEST_PROJECT);
		getUI().ensureThat(view("Package Explorer").isShowing());
	}
	
	/**
	 * Main test method.
	 */
	public void testShellMonitorSmoke() throws Exception {
		IUIContext ui = getUI();

		IShellMonitor sm = (IShellMonitor) ui.getAdapter(IShellMonitor.class);
		sm.add(new IShellConditionHandler() {
						
			public boolean test(Shell shell) {
				return shell.getText().equals(SHELL_TITLE);
			}

			public void handle(IUIContext ui) throws Exception {
				ui.click(new ButtonLocator(CANCEL_BUTTON_TEXT));
				ui.wait(new ShellDisposedCondition(SHELL_TITLE));
			}
		});
		
//		com.windowtester.internal.debug.ThreadUtil.startPrintStackTraces(10000);

		ui.contextClick(new TreeItemLocator(TEST_PROJECT, new ViewLocator("org.eclipse.jdt.ui.PackageExplorer")),
			"Delete");
		ui.contextClick(new TreeItemLocator(TEST_PROJECT, new ViewLocator("org.eclipse.jdt.ui.PackageExplorer")),
			"Delete");
	}

}