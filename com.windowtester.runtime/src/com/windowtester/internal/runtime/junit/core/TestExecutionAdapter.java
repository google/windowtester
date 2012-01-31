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
 * This adapter class provides default implementations for the
 * methods described by the {@link ITestExecutionListener} interface.
 */
public class TestExecutionAdapter implements ITestExecutionListener {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#exceptionCaught(java.lang.Throwable)
	 */
	public void exceptionCaught(Throwable e) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#testFinished()
	 */
	public void testFinished() {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#testFinishing()
	 */
	public void testFinishing() {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#testStarting(com.windowtester.runtime.test.TestIdentifier)
	 */
	public void testStarting(ITestIdentifier identifier) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#testStarted(com.windowtester.runtime.test.TestIdentifier)
	 */
	public void testStarted(ITestIdentifier identifier) {
		//no-op
	}
	
}
