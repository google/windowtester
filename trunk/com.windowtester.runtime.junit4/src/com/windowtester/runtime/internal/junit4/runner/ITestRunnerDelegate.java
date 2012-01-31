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
package com.windowtester.runtime.internal.junit4.runner;


import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;


/**
 * A strategy for running a given test class.
 * @author Phil Quitslund
 *
 */
public interface ITestRunnerDelegate {

	
	/**
	 * @see org.junit.runner.Runner#getDescription()
	 */
	Description getDescription();

	/**
	 * Run with this notifier in this exec context.
	 * @param notifier
	 * @param executionContext
	 */
	void run(RunNotifier notifier, IExecutionContextProvider executionContext);
	
}
