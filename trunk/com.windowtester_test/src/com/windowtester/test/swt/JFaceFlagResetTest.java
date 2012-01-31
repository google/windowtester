package com.windowtester.test.swt;

import org.eclipse.jface.dialogs.ErrorDialog;

import com.windowtester.runtime.swt.UITestCaseSWT;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
public class JFaceFlagResetTest {

	
	public static class TestNonUI extends TestCase {
		public void testFlagValue() {
			assertTrue(ErrorDialog.AUTOMATED_MODE);
		}
	}
	
	
	public static class TestUI extends UITestCaseSWT {
		public void testFlagValue() {
			assertFalse(ErrorDialog.AUTOMATED_MODE);
		}
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.windowtester.swt.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestNonUI.class);
		suite.addTestSuite(TestUI.class);
		suite.addTestSuite(TestNonUI.class);
		suite.addTestSuite(TestUI.class);
		suite.addTestSuite(TestNonUI.class);
		suite.addTestSuite(TestUI.class);
		suite.addTestSuite(TestUI.class);
		suite.addTestSuite(TestNonUI.class);
		//$JUnit-END$
		return suite;
	}

}
