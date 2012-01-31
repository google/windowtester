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
package com.windowtester.test.screencapture;

import java.io.File;

import junit.extensions.UITestCase;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.swt.IUIContext;
import com.windowtester.swt.WidgetLocator;
import com.windowtester.swt.WidgetSearchException;

/**
 * Validates that screenshots are taken at the right times (and ONLY the right times)
 * 
 * @author Phil Quitslund
 * @author Dan Rubel
 */
public class OldAPIScreenCaptureTest extends UITestCase
{
	private int expectedScreenShotCount;

	protected void setUp() {
		expectedScreenShotCount = getScreenShotCount();
	}

	protected void tearDown() throws Exception {
		assertEquals(expectedScreenShotCount, getScreenShotCount());
	}

	public void testOldAPIScreenCapture() throws WidgetSearchException {
		IUIContext ui = getUIContext();
		Widget fileMenu = ui.find(new WidgetLocator(MenuItem.class, "&File"));
		try {
			ui.click(fileMenu, "bogus");
			fail("Expected this to fail");
		}
		catch (WidgetSearchException e) {
			expectedScreenShotCount++;
			assertEquals(expectedScreenShotCount, getScreenShotCount());
		}
	}

//	/**
//	 * Expected behavior: one screenshot showing open popup menu.
//	 */
//	public void testFailedPopUpMenuItemFind() throws WidgetSearchException {
//		IUIContext ui = getUIContext();
//		Widget projectItem = ui.find(new WidgetLocator(TreeItem.class, TEST_PROJECT_NAME));
//		try {
//			ui.contextClick(projectItem, "bogus");
//			fail();
//		}
//		catch (WidgetSearchException e) {
//			expectedScreenShotCount++;
//			assertEquals(expectedScreenShotCount, getScreenShotCount());
//		}
//	}
//
//	/**
//	 * Expected behavior: one screenshot showing the dialog.
//	 */
//	public void testFailedFindInDialog() throws WidgetSearchException {
//		IUIContext ui = getUIContext();
//		TestMacros2.invokeNewWizard(ui);
//		try {
//			TestMacros2.clickButton(ui, "bogus");
//			fail();
//		}
//		catch (WidgetSearchException e) {
//			setAndAssertExpectedShotCount(1);
//		}
//
//	}
//
//	/**
//	 * Expected behavior: one screenshot for each dialog (2).
//	 */
//	public void testFailedFindInChainedDialog() throws WidgetSearchException {
//		IUIContext ui = getUIContext();
//
//		// setup the chained dialogs
//		ui.getDisplay().syncExec(new Runnable() {
//			public void run() {
//				final Shell shell = new Shell(getUIContext().getDisplay().getActiveShell(), SWT.APPLICATION_MODAL);
//				shell.setText("Another Dialog?");
//				shell.setLayout(new FillLayout());
//				Button OK = new Button(shell, SWT.NONE);
//				OK.setText("Yes!");
//				OK.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						MessageDialog.openWarning(shell, "Chained Dialog", "!");
//					}
//				});
//				Button cancel = new Button(shell, SWT.NONE);
//				cancel.setText("No, Thanks");
//				cancel.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						shell.dispose();
//					}
//				});
//
//				shell.pack();
//				shell.open();
//			}
//		});
//
//		ui.waitForShellShowing("Another Dialog?");
//		TestMacros2.clickButton(ui, "Yes!");
//
//		/*
//		 * There are now two dialogs open...
//		 */
//		try {
//			TestMacros2.clickButton(ui, "bogus");
//			fail();
//		}
//		catch (WidgetSearchException e) {
//			setAndAssertExpectedShotCount(2);
//		}
//	}
//
//	public void testFailedTreeItemFindInMainWindow() throws WidgetSearchException {
//
//		IUIContext ui = getUIContext();
//		Widget item = ui.find(new WidgetLocator(Tree.class, new ViewLocator("org.eclipse.ui.views.ResourceNavigator")));
//
//		try {
//			ui.click(item, "bogus/node");
//			fail();
//		}
//		catch (WidgetSearchException e) {
//			setAndAssertExpectedShotCount(1);
//		}
//	}
//
//	public void testFailedComboItemFind() throws WidgetSearchException {
//
//		IUIContext ui = getUIContext();
//		TestMacros2.openPreferences(ui, "Java/Compiler");
//		Widget combo = ui.find(new LabeledLocator(Combo.class, "Comp&iler compliance level:"));
//		try {
//			ui.click(combo, "bogus");
//			fail();
//		}
//		catch (Throwable t) {
//			setAndAssertExpectedShotCount(1);
//		}
//	}
//
//	private void setAndAssertExpectedShotCount(int expected) {
//		expectedScreenShotCount = expected;
//		assertEquals(expectedScreenShotCount, getScreenShotCount());
//	}

	/*
	 * NOTE: these are EXPECTED TO FAIL! Turn these ON to verify that "top-level"
	 * exceptions (e.g., unhandled exceptions) are generating screenshots. SUCCESS:
	 * failure in test (but NOT tearDown). FAILURE: failure in tearDown.
	 */

	// public void testFailedAssertionNoHandler() {
	// _expectedCaptures = 1;
	// fail("simulating user generated failure"); //should be a screenshot at teardown
	// }
	//	
	//	
	// public void testFailedWaitAssertionNoHandler() {
	// _expectedCaptures = 1;
	// getUIContext().wait(new ICondition() {
	// public boolean test() {
	// return false;
	// }
	// }, 100);
	// //should be a screenshot at teardown
	// }

	// TODO: need to do test for CCombos...

	////////////////////////////////////////////////////////////////////////////
	//
	// Utility
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Answer the number of screen shots in the normal location
	 * 
	 * @return the number of screen shots
	 */
	private static int getScreenShotCount() {
		return getScreenShotCount("wintest");
	}

	/**
	 * Answer the number of screen shots in the specified location
	 * 
	 * @return the number of screen shots
	 */
	private static int getScreenShotCount(String path) {
		int count = 0;
		File[] list = new File(path).listFiles();
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				if (isScreenShot(list[i]))
					count++;
			}
		}
		return count;
	}

	/**
	 * Determine if the specified file is a screen shot file
	 * 
	 * @param file the file
	 * @return <code>true</code> if the file is a screen shot file, else <code>false</code>
	 */
	private static boolean isScreenShot(File file) {
		return file.getName().startsWith(OldAPIScreenCaptureTest.class.getName()) && file.getName().endsWith(".png");
	}

}
