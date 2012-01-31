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
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import com.windowtester.internal.runtime.junit.core.IExecutionContext;
import com.windowtester.internal.runtime.junit.core.launcher.IApplicationLauncher;
import com.windowtester.runtime.internal.junit4.mirror.runners.InitializationError;
import com.windowtester.runtime.internal.junit4.runners.UITestClassMethodsRunner;
import com.windowtester.runtime.internal.junit4.runners.UITestClassRunner;

/**
 * <p>
 *
 * @author Phil Quitslund
 *
 */
public class JUnit4RunnerDelegate implements ITestRunnerDelegate, IExecutionContextProvider {

	private final Runner _runner;
	private IExecutionContextProvider _contextProvider;
	

	public JUnit4RunnerDelegate(Class<?> cls) throws InitializationError {
//		try {
			_runner   = new UITestClassRunner(cls, new UITestClassMethodsRunner(cls, this, this), (IExecutionContextProvider)this, this);
//		} catch (InitializationError e) {
//			throw new IllegalStateException(e); //TODO: reconsider this error-handling scheme
//		}
	}

	private Runner getRunner() {
		return _runner;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.junit4.runners.ITestRunnerDelegate#getDescription()
	 */
	public Description getDescription() {
		return getRunner().getDescription();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.junit4.runners.ITestRunnerDelegate#run(org.junit.runner.notification.RunNotifier, com.windowtester.runtime.internal.junit4.runner.IExecutionContextProvider)
	 */
	public void run(final RunNotifier notifier, IExecutionContextProvider execContext) {
		_contextProvider = execContext;
		getRunner().run(notifier);		
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.junit4.runner.IExecutionContextProvider#getExecutionContext()
	 */
	public IExecutionContext getExecutionContext() {
		return getContextProvider().getExecutionContext();
	}

	private IExecutionContextProvider getContextProvider() {
		return _contextProvider;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.junit4.runner.IExecutionContextProvider#getLauncher()
	 */
	public IApplicationLauncher getLauncher() {
		return getContextProvider().getLauncher();
	}
	
	
}
