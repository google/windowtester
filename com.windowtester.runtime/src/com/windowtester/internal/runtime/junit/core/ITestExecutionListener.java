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
package com.windowtester.internal.runtime.junit.core;


/**
 * Listen to the execution of a test.
 */
public interface ITestExecutionListener {
	
	/**
	 * The given test is starting.  This hook is provided for initialization.
	 */
	void testStarting(ITestIdentifier identifier);

	/**
	 * The given test is started (starting listeners have all been notified).
	 */
	void testStarted(ITestIdentifier identifier);
	
	/**
	 * The given exception was caught in test execution.
	 * @param e the caught exception
	 */
	void exceptionCaught(Throwable e);
	
	/**
	 * The test is finishing. (Note that in situations where test execution is wrapped
	 * in its own test thread, this should be called BEFORE leaving that test thread.)
	 */
	void testFinishing();

	/**
	 * The test is finished. (Note that in situations where test execution is wrapped
	 * in its own test thread, this should be called AFTER leaving that test thread.)
	 */
	void testFinished();
	
}
