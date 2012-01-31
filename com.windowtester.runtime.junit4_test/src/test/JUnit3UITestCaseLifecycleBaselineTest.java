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

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.UITestCaseSWT;


/**
 * @author Phil Quitslund
 *
 */
public class JUnit3UITestCaseLifecycleBaselineTest extends TestSuite {

	
	private static IUIContext cachedUI;
	
	public static class TestOne extends UITestCaseSWT {
	
		public void testCacheUI() {
			System.out.println("test one...");
			cachedUI = getUI();
		}
	}
	
	public static class TestTwo extends UITestCaseSWT {
		public void testVerifyNewUIDiffersFromCache() {
			System.out.println("test two...");		
			assertNotSame(getUI(), cachedUI);
		}
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TestOne.class);
		suite.addTestSuite(TestTwo.class);
		return suite;
	}
	
}
