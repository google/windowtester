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
package com.windowtester.runtime.common;

import junit.framework.TestCase;

import com.windowtester.internal.runtime.junit.core.ExecutionMonitor;
import com.windowtester.internal.runtime.junit.core.IExecutionContext;
import com.windowtester.internal.runtime.junit.core.IExecutionMonitor;
import com.windowtester.internal.runtime.junit.core.SequenceRunner;
import com.windowtester.internal.runtime.junit.core.ISequenceRunner.IRunnable;
import com.windowtester.internal.runtime.junit.core.launcher.IApplicationLauncher;
import com.windowtester.internal.runtime.junit.core.launcher.LauncherFactory;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.internal.TestClassManager;
import com.windowtester.runtime.util.TestMonitor;

/**
 * A common UI TestCase base class for SWT, RCP and Swing UI tests.
 */
public abstract class UITestCaseCommon extends TestCase {

	
	/**
	 * Cached UIContext instance.
	 */
	private IUIContext ui;
			
	/**
	 * Used manage execution state.
	 */
	private IExecutionContext executionContext;

	/**
	 * Used to launch the application under test.
	 */
	private IApplicationLauncher applicationLauncher;

	
	/**
	 * Manages onetime setup and teardown.
	 */
	private final static TestClassManager testClassWatcher = new TestClassManager() {
		public void firstRun(TestCase t) throws Exception {
			((UITestCaseCommon)t).oneTimeSetup();
		}
		public void lastRun(TestCase t) throws Exception {
			((UITestCaseCommon)t).oneTimeTearDown();
		}
	};
	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Instance Creation
	//
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * Create an instance.
	 */
	public UITestCaseCommon() {
		this((String)null);
	}

	/**
	 * Create an instance with the given name.
	 */
	public UITestCaseCommon(String testName) {
		this(testName, (Class<?>)null);
	}
	
	public UITestCaseCommon(String testName, Class<?> launchClass) {
		this(testName, launchClass, null);
	}
	
	
	/**
	 * Create an instance that will launch and test the specified application class.
	 * 
	 * @param launchClass - The application class to be launched by calling the static
	 *            main method with the specified arguments (see ) in a separate thread, or
	 *            <code>null</code> if no application is to be launched.
	 */
	public UITestCaseCommon(Class<?> launchClass) {
		this(launchClass, null);
	}

	/**
	 * Create an instance that will launch and test the specified application class.
	 * 
	 * @param launchClass - The application class to be launched by calling the static
	 *            main method with the specified arguments (see ) in a separate thread, or
	 *            <code>null</code> if no application is to be launched.
	 * @param launchArgs - The arguments to be passed to the static main method of the
	 *            application class to be launched, or <code>null</code> if no
	 *            application is to be launched.
	 */
	public UITestCaseCommon(Class<?> launchClass, String[] launchArgs) {
		this(null, launchClass, launchArgs);
	}
	
	
	public UITestCaseCommon(String testName, Class<?> launchClass, String[] launchArgs) {
		super(testName);
		//create a launcher for the given class and args (note: NoOP launcher class created
		//in case class is null
		applicationLauncher = LauncherFactory.create(launchClass, launchArgs);
		testClassWatcher.toRun(this);
	}

	/////////////////////////////////////////////////////////////////////////////////
	//
	// Test Lifecycle
	//
	/////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#runBare()
	 */
	public void runBare() throws Throwable {
		
		//decrement tests to run counter -- TODO: should this be moved?
//		--_testsToRun;

		
		//exec launch (NOTE: should this be in runner?)
		launchApp();
		
		//run test
		runUITest(new UITestRunnable() {
			public void run() throws Throwable {
				UITestCaseCommon.super.runBare();
			}
		});
		
	}
	public void runUITest(final UITestRunnable runnable) throws Throwable {
		
		//start test monitor ---> TODO: this should be in the exec monitor/runner
		TestMonitor.getInstance().beginTestCase(this);
				
		//get an execution monitor appropriate for this test type
		IExecutionMonitor execMonitor = getExecutionMonitor();
		
		//publish execution context
		ExecutionMonitor.setContext(getExecutionContext());
		
		//run the test in a separate monitored thread
		new SequenceRunner(execMonitor).exec(new IRunnable(){
			public void run() throws Throwable {
				runStarted();
				try {
					runnable.run();
				} finally {
					runFinished();
				}
			}
		});		
		
		//stop test monitor ---> TODO: this should be in the exec monitor/runner
		TestMonitor.getInstance().endTestCase();
	}

	private void launchApp() {
		IApplicationLauncher launcher = getApplicationLauncher();
		launch(launcher);
		launcher.launch();
	}

	
	
	/**
	 * Perform application launching, called before launching.
	 * Note: subclasses can participate in launching by attaching listeners
	 * here.  For example:
	 * <pre>
	 * la
	 * </pre>
	 * @param launcher
	 */
	protected void launch(IApplicationLauncher launcher) {
		//deafult is to do nothing
	}

	/**
	 * Get an execution monitor for the current test.
	 */
	protected IExecutionMonitor getExecutionMonitor() {
		return getExecutionContext().getExecutionMonitor();
	}
	
	
	private IApplicationLauncher getApplicationLauncher() {
		return applicationLauncher;
	}
	
	protected IExecutionContext getExecutionContext() {
		if (executionContext == null)
			executionContext = createExecutionContext();
		return executionContext;
	}
	
	/**
	 * Create an execution context instance for the current test.
	 * It is the subclass responsibility to create the appropriate execution context for its test type.
	 */
	protected abstract IExecutionContext createExecutionContext();
			

	private void runStarted() throws Exception {
		testClassWatcher.runStarted(this);
	}
	
	private void runFinished() throws Exception {
		testClassWatcher.runFinished(this);
	}
	
	/**
	 * Performs one time setup of the test fixture.  Called once per test class, before 
	 * setup.
	 */
	protected void oneTimeSetup() throws Exception {
		//default: no-op
	}
		
	/**
	 * Performs one time teardown of the test fixture.  Called once per test class, after 
	 * teardown.
	 */
	protected void oneTimeTearDown() throws Exception {
		//default: no-op
	}

	/////////////////////////////////////////////////////////////////////////////////
	//
	// UI Access
	//
	/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get the backing UI instance appropriate for this test execution.
	 */
	public IUIContext getUI() {
		if (ui == null)
			ui = getExecutionContext().getUI();
		return ui;	
	}

	
}
