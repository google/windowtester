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
package com.windowtester.runtime.swing;


import com.windowtester.internal.runtime.junit.core.IExecutionContext;
import com.windowtester.runtime.common.UITestCaseCommon;
import com.windowtester.runtime.swing.internal.junit.SwingExecutionContext;


/**
 * A UI TestCase tailored for executing Swing-based application tests. 
 */
public class UITestCaseSwing extends UITestCaseCommon {

	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Instance Creation
	//
	/////////////////////////////////////////////////////////////////////////////////

	public UITestCaseSwing() {
		super();
	}

	public UITestCaseSwing(String testName) {
		super(testName);
	}
	
	public UITestCaseSwing(String testName, Class launchClass) {
		super(testName, launchClass);
	}
	
	public UITestCaseSwing(String testName, Class launchClass, String[] launchArgs) {
		super(testName, launchClass, launchArgs);
	}
	
	public UITestCaseSwing(Class launchClass) {
		super(launchClass);
	}
	
	public UITestCaseSwing(Class launchClass, String[] launchArgs) {
		super(launchClass, launchArgs);
	}

	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Execution
	//
	/////////////////////////////////////////////////////////////////////////////////

	protected IExecutionContext createExecutionContext() {
		return new SwingExecutionContext();
	}
	

}
