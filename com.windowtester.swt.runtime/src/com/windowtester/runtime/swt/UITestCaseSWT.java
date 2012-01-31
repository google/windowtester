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
package com.windowtester.runtime.swt;




import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.runtime.junit.core.IExecutionContext;
import com.windowtester.internal.runtime.junit.core.launcher.IApplicationLauncher;
import com.windowtester.internal.runtime.junit.core.launcher.ILaunchListener;
import com.windowtester.runtime.common.UITestCaseCommon;
import com.windowtester.runtime.swt.internal.display.DisplayIntrospection;
import com.windowtester.runtime.swt.internal.junit.SWTExecutionContext;

/**
 * A UI TestCase tailored for executing SWT-based application tests. 
 *
 */
public class UITestCaseSWT extends UITestCaseCommon {
	
	
	//how long to wait for a display to be found by introspection
	protected static final long INTROSPECTION_TIMEOUT = 10000; //default from UITestCase

	/////////////////////////////////////////////////////////////////////////////////
	//
	// Instance Creation
	//
	/////////////////////////////////////////////////////////////////////////////////

	public UITestCaseSWT() {
		super();
	}

	public UITestCaseSWT(String testName) {
		super(testName);
	}
	
	public UITestCaseSWT(String testName, Class<?> launchClass) {
		super(testName, launchClass);
	}
	
	public UITestCaseSWT(String testName, Class<?> launchClass, String[] launchArgs) {
		super(testName, launchClass, launchArgs);
	}
	
	public UITestCaseSWT(Class<?> launchClass) {
		super(launchClass);
	}
	
	public UITestCaseSWT(Class<?> launchClass, String[] launchArgs) {
		super(launchClass, launchArgs);
	}

	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Execution
	//
	/////////////////////////////////////////////////////////////////////////////////

	/* Participate in the launch by waiting for display introspection to complete.
	 * @see com.windowtester.runtime.common.UITestCaseCommon#launch(com.windowtester.internal.runtime.junit.core.launcher.IApplicationLauncher)
	 */
	protected void launch(IApplicationLauncher launcher) {
		/**
		 * Notice this listener will only get notified in case this is a
		 * launch of a main class (otherwise a NoOpLauncher is used which
		 * does not notify listeners).  This is the right thing to do.
		 * (Display introspection will work either way but is not necessary
		 * in the RCP case.) 
		 */
		launcher.addListener(new ILaunchListener() {
			public void postFlight() {
				//System.out.println("introspecting...");
				Display display = new DisplayIntrospection(INTROSPECTION_TIMEOUT).syncIntrospect();
				if (display == null)
					throw new AssertionError("display introspection failed");
				//System.out.println("done introspecting...");
			}
			public void preFlight() {
				//do nothing
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.common.UITestCaseCommon#createExecutionContext()
	 */
	protected IExecutionContext createExecutionContext() {
		return new SWTExecutionContext();
	}
	
}
