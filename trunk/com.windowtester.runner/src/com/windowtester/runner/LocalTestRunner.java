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
package com.windowtester.runner;

import java.io.IOException;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.swt.widgets.Display;

import com.windowtester.runner.util.Logger;


/**
 * {@link WindowTesterRunner} delegates test execution to this class if it cannot be
 * delegated to org.eclipse.test.UITestApplication in the org.eclipse.test plugin. Much of
 * this is copied from org.eclipse.test.UITestApplication.
 */
public class LocalTestRunner
	implements IPlatformRunnable
	/* $codepro.preprocessor.if version >= 3.3 $ */
	, org.eclipse.equinox.app.IApplication
/* $codepro.preprocessor.endif $ */
{
	private static final String TEST_APPLICATION_OPTION = "-testApplication";
	private static final String DEFAULT_APP_3_0 = "org.eclipse.ui.ide.workbench"; //$NON-NLS-1$
	
	private static final String TEST_PRODUCT_OPTION = "-testProduct";

	
	/**
	 * The results from running the tests
	 */
	private int fTestRunnerResult = -1;

	/**
	 * The main entry point for {@link IApplication}. Cache the application context and
	 * call {@link #run(Object)} to launch the applications and run the tests.
	 * 
	 * @param context the application context
	 * @return the return value of the application
	 * @throws Exception
	 */
	/* $codepro.preprocessor.if version >= 3.3 $ */
	public Object start(org.eclipse.equinox.app.IApplicationContext context) throws Exception {
		appContext = context;
		String[] args = (String[]) appContext.getArguments().get(
			org.eclipse.equinox.app.IApplicationContext.APPLICATION_ARGS);
		if (args == null)
			args = new String[0];
		return run(args);
	}

	public void stop() {
		// TODO [author=Dan] stop the tests
	}

	private org.eclipse.equinox.app.IApplicationContext appContext;

	/* $codepro.preprocessor.endif $ */

	/**
	 * The main entry point for {@link IPlatformRunnable}. Launch the application and run
	 * the tests
	 * 
	 * @param args the argument(s) to pass to the application
	 * @return the return value of the application
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */
	public Object run(Object args) throws Exception {
		String appId = getProductApplication((String[]) args);
		if (appId == null)
			appId = getApplicationToRun((String[])args);
		Object application = getApplication(appId);
		queueTests(args);
		launchApplication(args, application);
		return new Integer(fTestRunnerResult);
	}

	/**
	 * Determine the application to run.
	 * 
	 * @return the application, or null if not even the default application is found.
	 */
	private Object getApplication(String appId) throws Exception {
		// Assume we are in 3.0 mode.
		// Find the name of the application as specified by the PDE JUnit launcher.
		// If no application is specified, the 3.0 default workbench application
		// is returned.
		
		IExtension extension = Platform.getExtensionRegistry().getExtension(Platform.PI_RUNTIME,
			Platform.PT_APPLICATIONS, appId);
		if (extension == null)
			fail("Failed to find " + Platform.PT_APPLICATIONS + " extension for " + appId);

		// If the extension does not have the correct grammar, throw an exception.
		// Otherwise, return the application object.
		IConfigurationElement[] elements = extension.getConfigurationElements();
		if (elements.length == 0)
			fail("Failed to find " + Platform.PT_APPLICATIONS + " child elements for " + appId);
		IConfigurationElement[] runs = elements[0].getChildren("run"); //$NON-NLS-1$
		if (runs.length == 0)
			fail("Failed to find " + Platform.PT_APPLICATIONS + " run child element for " + appId);
		Object application = runs[0].createExecutableExtension("class"); //$NON-NLS-1$
		if (application instanceof IPlatformRunnable)
			return application;
		/* $codepro.preprocessor.if version >= 3.3 $ */
		if (application instanceof org.eclipse.equinox.app.IApplication)
			return application;
		/* $codepro.preprocessor.endif $ */
		String className = application != null ? application.getClass().getName() : "null";
		fail(className + " does not implement IApplication or IPlatformRunnable");
		// fail throws RuntimeException, so this return is never reached
		return null;
	}

	/**
	 * Run the application
	 * 
	 * @param application the application to run
	 * @param args the argument(s) to pass to the application
	 */
	private void launchApplication(Object args, Object application) throws Exception {
		Object result;
		/* $codepro.preprocessor.if version >= 3.3 $ */
		if (application instanceof org.eclipse.equinox.app.IApplication)
			result = ((org.eclipse.equinox.app.IApplication) application).start(appContext);
		else
			/* $codepro.preprocessor.endif $ */
			result = ((IPlatformRunnable) application).run(args);
		if (!IPlatformRunnable.EXIT_OK.equals(result)) {
			System.err.println("UITestRunner: Unexpected result from running application " + application + ": "
				+ result);
		}
	}

	/**
	 * Wait for a window to open and then run the tests
	 * 
	 * @param args the argument(s) to pass to the application
	 */
	private void queueTests(Object args) {
		final Thread testThread = new Thread("Launch Tests Thread") {
			private boolean started = false;

			public void run() {
				pause(5000);
				final Display display = Display.getDefault();
				while (!started) {
					pause(1000);
					display.syncExec(new Runnable() {
						public void run() {
							started = display.getShells().length > 0;
						}
					});
				}
				display.asyncExec(new Runnable() {
					public void run() {
						while (display.readAndDispatch()) {
							// continue processing events until the application is "idle"
						}
						try {
							fTestRunnerResult = EclipseTestRunner.run(Platform.getCommandLineArgs());
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
		testThread.setPriority(Thread.MIN_PRIORITY);
		testThread.setDaemon(true);
		testThread.start();
	}

	/**
	 * The -testApplication argument specifies the application to be run. If the PDE JUnit
	 * launcher did not set this argument, then return the name of the default
	 * application. In 3.0, the default is the "org.eclipse.ui.ide.worbench" application.
	 */
	private String getApplicationToRun(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(TEST_APPLICATION_OPTION) && i < args.length - 1) //$NON-NLS-1$
				return args[i + 1];
		}
		Logger.log("No " + TEST_APPLICATION_OPTION + " specified so assuming " + DEFAULT_APP_3_0);
		return DEFAULT_APP_3_0;
	}

	/**
	 * The -testProduct arguments speciifes the product to be run. If this returns null, then look
	 * for the test application argument
	 * @param args
	 * @return
	 */
	private String getProductToRun(String[] args){
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(TEST_PRODUCT_OPTION) && i < args.length - 1) //$NON-NLS-1$
				return args[i + 1];
		}
		Logger.log("No " + TEST_PRODUCT_OPTION + " specified");
		return null;
	}
	
	/**
	 * get the application id from the product definition
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public String getProductApplication(String[] args) throws Exception {
		String productId = getProductToRun(args);
		// if -testProduct is not specified, look for application
		if (productId == null)
			return null;
		
		IExtension extension = Platform.getExtensionRegistry().getExtension(Platform.PI_RUNTIME,
			Platform.PT_PRODUCT, productId);
		
		if (extension == null)
			fail("Failed to find " + Platform.PT_PRODUCT + " extension for " + productId);

		// If the extension does not have the correct grammar, throw an exception.
		// Otherwise, return the application object.
		IConfigurationElement[] elements = extension.getConfigurationElements();
		if (elements.length == 0)
			fail("Failed to find " + Platform.PT_PRODUCT + " child elements for " + productId);
		String appName = elements[0].getAttribute("application");
		
		if (appName.length() == 0)
			fail("Failed to find " + Platform.PT_PRODUCT + " application element for " + productId);
		
		return appName;
		
	}
	
	
	/**
	 * Log the message and throw a {@link RuntimeException}
	 * 
	 * @param msg the message
	 */
	private void fail(String msg) {
		Logger.log(msg);
		throw new RuntimeException(msg);
	}

	/**
	 * Causes the currently executing thread to sleep (temporarily cease execution) for
	 * the specified number of milliseconds.
	 * 
	 * @param millis the length of time to sleep in milliseconds.
	 */
	public void pause(int millis) {
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) {
			// ignored
		}
	}
}
