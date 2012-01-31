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
package com.windowtester.runtime.junit4;


import static com.windowtester.runtime.internal.junit4.runners.UITestIntrospector.getLaunchArgs;
import static com.windowtester.runtime.internal.junit4.runners.UITestIntrospector.getLaunchClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import com.windowtester.internal.runtime.junit.core.IExecutionContext;
import com.windowtester.internal.runtime.junit.core.launcher.IApplicationLauncher;
import com.windowtester.internal.runtime.junit.core.launcher.LauncherFactory;
import com.windowtester.runtime.internal.junit4.ExecutionMonitor;
import com.windowtester.runtime.internal.junit4.mirror.runners.InitializationError;
import com.windowtester.runtime.internal.junit4.runner.IExecutionContextProvider;
import com.windowtester.runtime.internal.junit4.runner.ITestRunnerDelegate;
import com.windowtester.runtime.internal.junit4.runner.RunnerFactory;
import com.windowtester.runtime.internal.junit4.runners.UITestIntrospector.NoMain;
/**
 * A <code>UITestRunner</code> runs UI tests and notifies a {@link org.junit.runner.notification.RunNotifier}
 * of significant events as it does so. Concrete subclasses of <code>UITestRunner</code> are provided to run
 * SWT and Swing UI tests.
 * <p>
 *
 * @author Phil Quitslund
 *
 */
public abstract class UITestRunner extends Runner implements IExecutionContextProvider {

	/**
	 * The <code>Launch</code> annotation specifies the class to be launched and (optionally arguments) 
	 * when a class annotated with <code>@RunWith(UITestClassRunner.class)</code> is run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Launch {
		/**
		 * Specifies the class whose <code>main</code> method is the entry-point of the application under-test.
		 */
		Class<?> main() default NoMain.class;
		/**
		 * Specifies the arguments to pass to the <code>main</code> method when it is executed.
		 */
		String[] args() default {};
	}

	
	private final ITestRunnerDelegate _runnerDelegate;
	private final IApplicationLauncher _launcher;
	
	private IExecutionContext _execContext;

	
	public UITestRunner(Class<?> klass) throws InitializationError {
		_runnerDelegate = RunnerFactory.createRunner(klass);	
		_launcher       = LauncherFactory.create(getLaunchClass(klass), getLaunchArgs(klass));
	}
		
	private ITestRunnerDelegate getRunner() {
		return _runnerDelegate;
	}
	
	/* (non-Javadoc)
	 * @see org.junit.runner.Runner#getDescription()
	 */
	@Override
	public Description getDescription() {
		return getRunner().getDescription();
	}

	/* (non-Javadoc)
	 * @see org.junit.runner.Runner#run(org.junit.runner.notification.RunNotifier)
	 */
	@Override
	public void run(RunNotifier notifier) {
		setGlobalRunContext();
		System.out.println("calling run");
		getRunner().run(notifier, this);
	}

	private void setGlobalRunContext() {
		ExecutionMonitor.setContext(getGlobalContext());
	}

	protected abstract IExecutionContext getGlobalContext();

	protected abstract IExecutionContext createExecContext();

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.junit4.runner.IExecutionContextProvider#getExecutionContext()
	 */
	public IExecutionContext getExecutionContext() {
		if (_execContext == null)
			_execContext = createExecContext();
		return _execContext;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.junit4.runner.IExecutionContextProvider#getLauncher()
	 */
	public IApplicationLauncher getLauncher() {
		return _launcher;
	}
}
