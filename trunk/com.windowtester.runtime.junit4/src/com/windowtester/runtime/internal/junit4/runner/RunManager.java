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

import com.windowtester.internal.runtime.junit.core.IExecutionContext;
import com.windowtester.internal.runtime.junit.core.IExecutionMonitor;
import com.windowtester.internal.runtime.junit.core.SequenceRunner;
import com.windowtester.internal.runtime.junit.core.ISequenceRunner.IRunnable;
import com.windowtester.runtime.internal.junit4.ExecutionMonitor;
import com.windowtester.runtime.internal.junit4.JUnit4TestId;
import com.windowtester.runtime.util.TestMonitor;

/**
 * <p>
 *
 * @author Phil Quitslund
 *
 */
public class RunManager {

	
	private final ITestRunnerDelegate _runner;
	private final IExecutionContextProvider _contextProvider;

	public RunManager(ITestRunnerDelegate runner, IExecutionContextProvider contextProvider) {
		_runner = runner;
		_contextProvider = contextProvider;
	}

	public ITestRunnerDelegate getRunner() {
		return _runner;
	}
	
	public IExecutionContext getExecContext() {
		return getContextProvider().getExecutionContext();
	}

	private IExecutionContextProvider getContextProvider() {
		return _contextProvider;
	}
	
	@SuppressWarnings("deprecation")
	public void run(IRunnable runnable, Description description) throws Throwable {
		System.out.println("run manager running");
		
		//exec launch (NOTE: should this be in runner?)
		launchApp();
		
		//start test monitor ---> TODO: this should be in the exec monitor/runner
		TestMonitor.getInstance().beginTest(new JUnit4TestId(description));
		System.out.println("RunManager starting test: " + description);
		
		
		//get an execution monitor appropriate for this test type
		IExecutionMonitor execMonitor = getExecutionMonitor();
		
		
		ExecutionMonitor.setContext(getExecContext());
		
		//run the test in a separate monitored thread
		new SequenceRunner(execMonitor).exec(runnable);
		
		//stop test monitor ---> TODO: this should be in the exec monitor/runner
		TestMonitor.getInstance().endTestCase();
	}

	protected void launchApp() {
		getContextProvider().getLauncher().launch();
	}

	protected IExecutionMonitor getExecutionMonitor() {
		return getExecContext().getExecutionMonitor();
	}


}
