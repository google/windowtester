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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.windowtester.runner.util.Logger;


/**
 * Wrappers an application that resides in another plugin that is not directly
 * accessible from this plugin.
 * 
 */
public class WrapperedTestRunner
	implements IPlatformRunnable
{
	private final String bundleId;
	private final String appClassName;
	private IPlatformRunnable app;

	public WrapperedTestRunner(String bundleId, String appClassName) {
		this.bundleId = bundleId;
		this.appClassName = appClassName;
	}

	/**
	 * Determine if the wrappered application can be successfully resolved, accessed and
	 * started.
	 * 
	 * @param args an array of {@link String} arguments
	 * @return <code>true</code> if the application can be started, else
	 *         <code>false</code>
	 */
	public boolean canStart(Object args) {
		initApp(args);
		return app != null;
	}

	/**
	 * Called to start the application
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object run(Object args) throws Exception {
		initApp(args);
		if (app == null)
			return new Integer(1);
		return app.run(args);
	}

	/**
	 * Attempt to access the bundle and instantiate the application.
	 * 
	 * @param args an array of {@link String} arguments
	 */
	private void initApp(Object rawArgs) {
		if (app != null)
			return;

		// Access the bundle

		Bundle bundle = Platform.getBundle(bundleId);
		if (bundle == null) {
			Logger.log("Bundle " + bundleId + " cannot be found.");
			return;
		}
		int state = bundle.getState();
		Logger.log("Bundle " + bundleId + " is " + getBundleStateName(state));
		if (state != Bundle.RESOLVED && state != Bundle.STARTING && state != Bundle.ACTIVE) {
			Logger.log("Bundle " + bundleId + " cannot be accessed.");
			return;
		}

		// Load the application class

		Class appClass;
		try {
			appClass = bundle.loadClass(appClassName);
		}
		catch (ClassNotFoundException e) {
			Logger.log("Cannot find class " + appClassName + " in bundle " + bundleId);
			return;
		}
		catch (IllegalStateException e) {
			Logger.log("Cannot access class " + appClassName + " in bundle " + bundleId);
			return;
		}

		// Instantiate the application instance

		try {
			app = (IPlatformRunnable) appClass.newInstance();
		}
		catch (InstantiationException e) {
			Logger.log("Cannot instantiate " + appClassName + " in bundle " + bundleId);
			return;
		}
		catch (IllegalAccessException e) {
			Logger.log("Cannot access instance of " + appClassName + " in bundle " + bundleId);
			return;
		}
		catch (ClassCastException e) {
			Logger.log("Cannot cast instance of " + appClassName + " in bundle " + bundleId + " to IPlatformRunnable");
			return;
		}

		// If this is the Eclipse UITestApplication, make additional checks
		// because UITestApplication can fail with poor error messages

		if (!appClassName.equals("org.eclipse.pde.internal.junit.runtime.UITestApplication"))
			return;

		if (!(rawArgs instanceof String[])) {
			Logger.logStackTrace("Illegal argument - must be array of String");
			return;
		}

		Object application;
		try {
			application = getApplication((String[]) rawArgs);
		}
		catch (CoreException e) {
			application = "Failed to determine application-under-test";
			Logger.log((String) application, e);
		}
		if (!(application instanceof IPlatformRunnable)) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter writer = new PrintWriter(stringWriter);
			if (application instanceof String)
				writer.println((String) application);
			else if (application != null)
				writer.println("Application " + application.getClass() + " does not implement "
					+ IPlatformRunnable.class);
			else
				writer.println("Failed to locate application-under-test");
			writer.println("Specify the identifier for the application-under-test using one of the following:");
			writer.println("1) -testApplication command line argument");
			writer.println("2) via a branding plug-in");
			Logger.log(stringWriter.toString());
			app = null;
		}
	}

	/**
	 * Answer the name for the specified bundle state
	 * 
	 * @param state the bundle state
	 * @return the bundle state name (not <code>null</code>)
	 */
	private String getBundleStateName(int state) {
		switch (state) {
			case Bundle.UNINSTALLED :
				return "uninstalled";
			case Bundle.INSTALLED :
				return "installed";
			case Bundle.RESOLVED :
				return "resolved";
			case Bundle.STARTING :
				return "starting";
			case Bundle.ACTIVE :
				return "active";
			case Bundle.STOPPING :
				return "stopping";
			default :
				return "unknown state: " + state;
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Support specific to UITestApplication
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Return the application to run, or an error message (String) if not even the default
	 * application is found.
	 */
	private Object getApplication(String[] args) throws CoreException {
		// Find the name of the application as specified by the PDE JUnit launcher.
		// If no application is specified, the 3.0 default workbench application
		// is returned.
		String applicationToRun = getApplicationToRun(args);
		IExtension extension = Platform.getExtensionRegistry().getExtension(Platform.PI_RUNTIME,
			Platform.PT_APPLICATIONS, applicationToRun);

		if (extension == null)
			return "Failed to locate extension " + Platform.PI_RUNTIME + "." + Platform.PT_APPLICATIONS // extension point
				+ " with id=" + applicationToRun; // application identifier

		// If the extension does not have the correct grammar, return an error message.
		// Otherwise, return the application object.
		IConfigurationElement[] elements = extension.getConfigurationElements();
		if (elements.length > 0) {
			IConfigurationElement[] runs = elements[0].getChildren("run"); //$NON-NLS-1$
			if (runs.length > 0) {
				return runs[0].createExecutableExtension("class");
			}
		}
		return "Found extension " + Platform.PI_RUNTIME + "." + Platform.PT_APPLICATIONS // extension point
			+ " with id=" + applicationToRun + " but extension does not have the correct <run class=\"...\" grammer";
	}

	/**
	 * COPIED from Eclipse 3.3 UITestApplication
	 * <p>
	 * The -testApplication argument specifies the application to be run. If the PDE JUnit
	 * launcher did not set this argument, then return the name of the default
	 * application. In 3.0, the default is the "org.eclipse.ui.ide.worbench" application.
	 */
	private String getApplicationToRun(String[] args) {
		IProduct product = Platform.getProduct();
		if (product != null)
			return product.getApplication();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-testApplication") && i < args.length - 1) //$NON-NLS-1$
				return args[i + 1];
		}
		return "org.eclipse.ui.ide.workbench";
	}
}
