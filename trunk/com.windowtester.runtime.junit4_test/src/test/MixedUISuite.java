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
package test;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.junit4.runners.JUnit38ClassRunner;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;

/**
 * A suite that mixes JUnit3 and JUnit4 tests.
 * 
 * @author Phil Quitslund
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( { 
//	MixedUISuite.JUnit3Suite.class, //not working
	NewProjectJUnit4Test.class, 
	NewProjectJUnit3Test.class, 
	MixedUISuite.JUnit4Suite.class

})
public class MixedUISuite {

	//Not working --- TODO: J4Suite{J3Suite}
	public static class JUnit3Suite {
		public static junit.framework.Test suite() {
			TestSuite suite = new TestSuite();
			suite.addTestSuite(OpenAndClosePreferencesTest.class);
			return suite;
		}
	}
	
	@RunWith(Suite.class)
	@SuiteClasses(OpenAndClosePreferencesTest.class)
	public static class JUnit4Suite {
		
	}
	
	@RunWith(JUnit38ClassRunner.class)
	public static class OpenAndClosePreferencesTest extends UITestCaseSWT {
		
		public void testOpenAndClosePrefs() throws Exception {
			IUIContext ui = getUI();
			ui.click(new MenuItemLocator("Window/Preferences..."));
			ui.wait(new ShellShowingCondition("Preferences"));
			ui.click(new ButtonLocator("Cancel"));
			ui.wait(new ShellDisposedCondition("Preferences"));
		}
	}
	
	

}
