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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.windowtester.runtime.swt.junit4.TestRunnerSWT;
import com.windowtester.runtime.util.TestMonitor;

/**
 *
 * @author Phil Quitslund
 *
 */
@RunWith(TestRunnerSWT.class)
public class TestRunnerSWTLifecycleTest {

	private static final String TEST_CLASS_NAME = TestRunnerSWTLifecycleTest.class.getName();
	
	@BeforeClass
	public static void verifyBeforeClassTestName() {
		assertEquals(TEST_CLASS_NAME + "_verifyBeforeClassTestName", TestMonitor.getInstance().getCurrentTestCaseID());	
	}
	
	@AfterClass
	public static void verifyAfterClassTestName() {
		assertEquals(TEST_CLASS_NAME + "_verifyAfterClassTestName", TestMonitor.getInstance().getCurrentTestCaseID());	
	}

	@Before
	public void verifyBeforeTestName() {
		assertEquals(TEST_CLASS_NAME + "_verifyTestName", TestMonitor.getInstance().getCurrentTestCaseID());	
	}

	@After
	public void verifyAfterTestName() {
		assertEquals(TEST_CLASS_NAME +  "_verifyTestName", TestMonitor.getInstance().getCurrentTestCaseID());	
	}
	
	
	@Test
	public void verifyTestName() {
		assertEquals(TEST_CLASS_NAME +  "_verifyTestName", TestMonitor.getInstance().getCurrentTestCaseID());	
	}
	
}
