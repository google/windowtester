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
/**
 * 
 */
package com.windowtester.runtime.swt.internal.abbot;

import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.TestMonitor;

public class ScreenCapturingExceptionListener implements IExceptionListener {

	protected final Display _display;

	/**
	 * Create an instance.
	 * @param display
	 */
	public ScreenCapturingExceptionListener(Display display) {
		_display = display;
	}

	/**
	 * @see com.windowtester.runtime.swt.internal.abbot.IExceptionListener#preException(java.lang.String)
	 */
	public void preException(String desc) {
		/*
		 * Pre-exception handling only happens in test case executions.
		 * NOTE: this is just a safety: this listener should not be registered
		 * in the non-testcase scenario but this provides sanity if it was....
		 */
		if (!TestMonitor.getInstance().isTestRunning())
			return; 
		
		String testcaseID = TestMonitor.getInstance().getCurrentTestCaseID();
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "Creating screenshot: " + desc + " for testcase: " + testcaseID);
        //TODO: make this filename format user configurable
		//skipping description for now (to be consistent for all errors)
		ScreenCapture.createScreenCapture(testcaseID /*+ "_" + desc*/);
	}
}