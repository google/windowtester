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

import java.lang.reflect.Method;

import org.junit.runner.notification.RunNotifier;

import com.windowtester.runtime.internal.junit4.mirror.runners.TestClassMethodsRunner;
import com.windowtester.runtime.internal.junit4.mirror.runners.TestMethodRunner;
import com.windowtester.runtime.internal.junit4.runner.IExecutionContextProvider;
import com.windowtester.runtime.internal.junit4.runner.ITestRunnerDelegate;

/**
 *
 * @author Phil Quitslund
 *
 */
public class UITestClassMethodsRunner extends TestClassMethodsRunner {

	private final ITestRunnerDelegate _runner;
	private final IExecutionContextProvider _contextProvider;

	public UITestClassMethodsRunner(Class<?> klass, ITestRunnerDelegate runner, IExecutionContextProvider contextProvider) {
		super(klass);
		_runner = runner;
		_contextProvider = contextProvider;
	}

	
	@Override
	protected TestMethodRunner createMethodRunner(Object test, Method method,
			RunNotifier notifier) {
		return new UITestMethodRunner(test, method, notifier, methodDescription(method), _runner, _contextProvider);
	}


	
	
}
