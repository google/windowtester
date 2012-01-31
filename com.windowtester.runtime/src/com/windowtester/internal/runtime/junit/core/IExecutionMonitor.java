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
 * Monitors the execution of a test, informing associated {@link ITestExecutionListener}s
 * along the way.  In addition, monitors are responsible for providing waitForFinished Logic.
 */
public interface IExecutionMonitor extends ITestExecutionListener {

	/**
	 * Add this listener to the test execution.
	 * @param listener the listener to add
	 */
	void addListener(ITestExecutionListener listener);
	/**
	 * Remove this listener.
	 * @param listener the listener to remove
	 */
	void removeListener(ITestExecutionListener listener);
	
	/**
	 * Perform a wait until the test execution is finished.
	 */
	void waitUntilFinished() throws Throwable;
		
	/**
	 * Get the exception cache for this monitor.
	 */
	public TestExceptionCache getExceptionCache();
}
