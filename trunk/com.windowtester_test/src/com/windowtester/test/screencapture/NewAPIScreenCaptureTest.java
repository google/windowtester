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
import java.util.concurrent.TimeUnit;

import abbot.Platform;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.ComboItemLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.ListItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.ShellLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.TestMonitor;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.util.TypingLinuxHelper;

public class NewAPIScreenCaptureTest extends BaseTest {

	private static final String SSHOT_DIR = "wintest";

	private static final String PREFERENCES_MENU_ITEM_PATH = "Window/&Preferences(...)?"; //3.4M7+-safe

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		clearExistingScreenShots(SSHOT_DIR);
	}

	//tests that our harness works
	public void testHarnessSanity() {
		int expectedScreenShotCount = getScreenShotCount();
		String testcaseID = TestMonitor.getInstance().getCurrentTestCaseID();
		ScreenCapture.createScreenCapture(testcaseID /*+ "_" + desc*/);
		assertScreenCaptureCount(++expectedScreenShotCount);
	}

	public void testNonExistentMenuItem() {
		int expectedScreenShotCount = getScreenShotCount();
		IUIContext ui = getUI();
		try {
			ui.click(new MenuItemLocator("&File/Bogus"));
			doFail();
		} catch (WidgetSearchException e) {
			e.printStackTrace();
			//3 for the menu retries
			assertScreenCaptureCount(expectedScreenShotCount+=3);
		}
	}

	public void testWaitTimeOut() {
		int expectedScreenShotCount = getScreenShotCount();
		IUIContext ui = getUI();
		try {
			ui.wait(new ICondition() {
				public boolean test() {
					return false;
				}
			}, 1);
			doFail();
		} catch (WaitTimedOutException e) {
			e.printStackTrace();
			assertScreenCaptureCount(++expectedScreenShotCount);
		}
	}

	public void testNonExistentTreeItem() throws WidgetSearchException {
		int expectedScreenShotCount = getScreenShotCount();
		IUIContext ui = getUI();
		if (Platform.isOSX())
			ui.keyClick(WT.COMMAND, ',');
		else
			ui.click(new MenuItemLocator(PREFERENCES_MENU_ITEM_PATH));
		ui.wait(new ShellShowingCondition("Preferences"));
		try {
			ui.click(new TreeItemLocator("General/Bogus"));
			doFail();
		} catch (WidgetSearchException e) {
			e.printStackTrace();
			assertScreenCaptureCount(++expectedScreenShotCount);
		}
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}

	
	public void testNonExistentCombo() throws WidgetSearchException {
		int expectedScreenShotCount = getScreenShotCount();
		IUIContext ui = getUI();
		try {
			ui.click(new ComboItemLocator("Bogus"));
			doFail();
		} catch (WidgetSearchException e) {
			e.printStackTrace();
			assertScreenCaptureCount(++expectedScreenShotCount);
		}
	}
	
	public void XtestNonExistentComboItemFails() throws WidgetSearchException {
		int expectedScreenShotCount = getScreenShotCount();
		IUIContext ui = getUI();
		if (Platform.isOSX())
			ui.keyClick(WT.COMMAND, ',');
		else
			ui.click(new MenuItemLocator(PREFERENCES_MENU_ITEM_PATH));
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new TreeItemLocator("Java/Debug"));
		
		try {
			ui.click(new ComboItemLocator("XXX"));
			doFail();
		} catch (WidgetSearchException e) {
			e.printStackTrace();
			assertScreenCaptureCount(++expectedScreenShotCount);
		}
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}
	
	public void testNonExistentButton() throws WidgetSearchException {
		int expectedScreenShotCount = getScreenShotCount();
		IUIContext ui = getUI();
		try {
			ui.click(new ButtonLocator("Bogus"));
			doFail();
		} catch (WidgetSearchException e) {
			e.printStackTrace();
			assertScreenCaptureCount(++expectedScreenShotCount);
		}
	}

	public void testNonExistentList() throws WidgetSearchException {
		int expectedScreenShotCount = getScreenShotCount();
		IUIContext ui = getUI();
		try {
			ui.click(new ListItemLocator("Bogus"));
			doFail();
		} catch (WidgetSearchException e) {
			e.printStackTrace();
			assertScreenCaptureCount(++expectedScreenShotCount);
		}
	}
	
	
	public void testNonExistentContextMenuItemInTree()
			throws Exception {

		int expectedScreenShotCount = getScreenShotCount();
		IUIContext ui = getUI();

		createJavaProject("TestProject");
		closeWelcomePageIfNecessary();
		openView("Java/Package Explorer");
		ui.click(new TreeItemLocator("TestProject", new ViewLocator("org.eclipse.jdt.ui.PackageExplorer"))); //verify project 
		try {

			ui.contextClick(new TreeItemLocator("TestProject", new ViewLocator("org.eclipse.jdt.ui.PackageExplorer")), "New/Bogus");
			doFail();
		} catch (WidgetSearchException e) {
			e.printStackTrace();
			//3 for the menu retries
			assertScreenCaptureCount(expectedScreenShotCount+=3);
		}
	}

	private void assertScreenCaptureCount(final int expectedScreenShotCount) {
		getUI().wait(new ICondition() {
			public boolean test() {
				return expectedScreenShotCount == getScreenShotCount();
			}
			@Override
			public String toString() {
				return "screenshot count to be " + expectedScreenShotCount + " but is: " + getScreenShotCount();
			}
		}, 7000);
//		assertEquals(expectedScreenShotCount, getScreenShotCount());
	}

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
		return getScreenShotCount(SSHOT_DIR);
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
		return file.getName().startsWith(
				NewAPIScreenCaptureTest.class.getName())
				&& file.getName().endsWith(".png");
	}

	private void clearExistingScreenShots(String path) {
		File[] list = new File(path).listFiles();
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				if (isScreenShot(list[i]))
					list[i].delete();
			}
		}
	}

	private void doFail() {
		fail("Expected this to fail... but it passed instead");
	}

	private void createJavaProject(String projectName)
			throws WidgetSearchException, WaitTimedOutException {
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new LabeledTextLocator("&Project name:"));
		ui.enterText(projectName);
		ui.click(new ButtonLocator("Finish"));

		//deal with "Open Associated Perspective?" dialog
		ui.wait(new TimeElapsedCondition(TimeUnit.MILLISECONDS, 3000));
		if(new ShellLocator("Open Associated Perspective?").isVisible(ui)){
			ui.click(new ButtonLocator("Yes"));
			ui.wait(new ShellDisposedCondition("Open Associated Perspective?"));
		}
		
		ui.wait(new ShellDisposedCondition("New Java Project"));
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();			
		}
	}
	
	protected void openView(String viewName) throws WidgetSearchException {
		IUIContext ui = getUI();
	    ui.click(new MenuItemLocator("&Window/Show &View/&Other.*")); //3.* safe path
        ui.wait(new ShellShowingCondition("Show View"));
        ui.click(new TreeItemLocator(viewName));
        ui.click(new ButtonLocator("OK"));
	}
	
}