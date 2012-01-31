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
package com.windowtester.runtime.internal.junit4.runners;

import static com.windowtester.runtime.internal.junit4.TestDescription.fromMethod;

import java.lang.reflect.Method;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.windowtester.internal.runtime.junit.core.ISequenceRunner.IRunnable;
import com.windowtester.runtime.internal.junit4.mirror.runners.BeforeAndAfterRunner;
import com.windowtester.runtime.internal.junit4.mirror.runners.InitializationError;
import com.windowtester.runtime.internal.junit4.mirror.runners.TestClassRunner;
import com.windowtester.runtime.internal.junit4.runner.IExecutionContextProvider;
import com.windowtester.runtime.internal.junit4.runner.ITestRunnerDelegate;
import com.windowtester.runtime.internal.junit4.runner.RunManager;

public class UITestClassRunner extends TestClassRunner {

		
	private final IExecutionContextProvider _executionContextProvider;
	private final ITestRunnerDelegate _runnerDelegate;

	public UITestClassRunner(Class<?> klass, Runner runner, IExecutionContextProvider contextProvider, ITestRunnerDelegate runnerDelegate) throws InitializationError {
		super(klass, runner);
		_executionContextProvider = contextProvider;
		_runnerDelegate           = runnerDelegate;
	}


	@Override
	public void run(final RunNotifier notifier) {
		BeforeAndAfterRunner runner = new BeforeAndAfterRunner(getTestClass(),
				BeforeClass.class, AfterClass.class, null) {		
			@Override
			protected void runUnprotected() {
				fEnclosedRunner.run(notifier);
			}
		
			@Override
			protected void addFailure(Throwable targetException) {
				notifier.fireTestFailure(new Failure(getDescription(), targetException));
			}
			@Override
			protected void invokeMethod(final Method method) throws Exception {
				try {
					new RunManager(_runnerDelegate, _executionContextProvider).run(new IRunnable() {
						public void run() throws Throwable {
							method.invoke(fTest);
						}
					}, fromMethod(method));
				} catch (Throwable e) {    
					addFailure(e);
				}
			}
		};
		runner.runProtected();
	}



}
