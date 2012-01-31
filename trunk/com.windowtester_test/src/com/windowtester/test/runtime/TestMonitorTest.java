package com.windowtester.test.runtime;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.util.TestMonitor;


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
public class TestMonitorTest extends UITestCaseSWT {

	@Override
	public void setUp() {
		assertIsRunning();
	}

	public void testGetCurrentTestCaseID() {
		assertIsRunning();
		assertTestIdEquals(getIdForMethod("testGetCurrentTestCaseID"));
	}
	
	public void testGetCurrentTestCaseID2() {
		assertIsRunning();
		assertTestIdEquals(getIdForMethod("testGetCurrentTestCaseID2"));
	}
	
	public void testForceStoppedTest() {
		monitor().beginTestCase(null);
		assertIsNotRunning();
		//needed for teardown
		monitor().beginTestCase(this);
	}
	
	public void testForceStoppedTest2() {
		monitor().beginTest(null);
		assertIsNotRunning();
		//needed for teardown
		monitor().beginTestCase(this);
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		assertIsRunning();
	}

	
	////////////////////////////////////////////////////////////////////////
	//
	// Assertion Helpers
	//
	////////////////////////////////////////////////////////////////////////
	
	
	 private String getIdForMethod(String methodName) {
		return getIdForMethod(getClass(), methodName);
	}
	
	static String getIdForMethod(Class<?> cls, String methodName) {
		return cls.getName() + "_" + methodName;
	}


	static  void assertTestIdEquals(String id) {
		assertEquals(id, monitor().getCurrentTestCaseID());		
	}

	static TestMonitor monitor() {
		return TestMonitor.getInstance();
	}

	static void assertIsRunning() {
		assertTrue("test monitor should be running", monitor().isTestRunning());
	}

	static void assertIsNotRunning() {
		assertTrue("test monitor should not be running", !monitor().isTestRunning());
	}
	
}
