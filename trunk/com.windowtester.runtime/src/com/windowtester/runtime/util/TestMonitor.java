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
package com.windowtester.runtime.util;

import junit.framework.TestCase;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.runtime.junit.core.ITestIdentifier;
import com.windowtester.internal.runtime.test.JUnit3TestId;
import com.windowtester.internal.runtime.test.NoRunningTestId;
import com.windowtester.internal.runtime.test.TestId;

/**
 * Monitors the current test run.
 * 
 */
public class TestMonitor {

	private volatile ITestIdentifier _runningTest = noTest();
	
	/* Singleton instance */
	static TestMonitor _monitor;
	
	/**
	 * @return Singleton monitor instance
	 */
	public static TestMonitor getInstance() {
		if (_monitor == null)
			_monitor = new TestMonitor();
		return _monitor;
	}
	
	private ITestIdentifier getRunningTest() {
		return _runningTest;
	}
	
    /**
     * Get a string representation of the current running test.
     */
    public String getCurrentTestCaseID() {
        return getRunningTest().getName();
    }
   		
    private void setRunningTest(ITestIdentifier runningTest) {
    	_runningTest = runningTest;
	}
    
    //NOTE: arguments must not be null
    public static String getId(Class<?> cls, String testCaseName) {
    	if (cls == null)
    		throw new IllegalArgumentException("class must not be null");
    	if (testCaseName == null)
    		throw new IllegalArgumentException("test case name must not be null");
    	return cls.getName() + "_" + testCaseName;
    }
    
    /** 
     *  Notify the TestMonitor that a new TestCase is starting. Passing <code>null</code>
     *  signals that a test has ended (or that none is running).
     *  @deprecated use {@link TestMonitor#beginTest(ITestIdentifier)} instead.
     */
    public void beginTestCase(TestCase testcase) {
    	if (testcase == null)
    		endTestCase(); //set test to none
    	else
    		beginTest(new JUnit3TestId(testcase));
    }
    
    /** 
     *  Notify the TestMonitor that a new Test is starting. Passing <code>null</code>
     *  signals that a test has ended (or that none is running).
     */
    public void beginTest(ITestIdentifier testId) {
    	if (testId == null)
    		endTestCase();
    	else {
    		logTestStart(testId);
    		setRunningTest(testId);
    	}
	}

	//TODO: this is probably NOT the best place for this to happen --- consider moving elsewhere
	private void logTestStart(ITestIdentifier testId) {
		Logger.log("UI test starting: " + testId.getName());
	}

	/**
     *  Notify the TestMonitor that a TestCase is ending.
     * 
     */
    public void endTestCase() {
        setRunningTest(noTest());
    }
	
    /**
     * Check to see if a test is running.  This can be used to see whether the 
     * runtime is executing in the context of a test run or a recording session.
     * @return true if a UITestCase is running
     */
    public boolean isTestRunning() {
    	return _runningTest != noTest();
    }


	private static NoRunningTestId noTest() {
		return TestId.none();
	}
	
}
