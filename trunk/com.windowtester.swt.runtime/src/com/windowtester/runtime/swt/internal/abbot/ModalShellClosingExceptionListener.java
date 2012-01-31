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
package com.windowtester.runtime.swt.internal.abbot;

import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.swt.internal.ExceptionHandlingHelper;
import com.windowtester.runtime.util.TestMonitor;

public class ModalShellClosingExceptionListener extends ScreenCapturingExceptionListener {

	/**
	 * Create an instance.
	 * @param display
	 */
	public ModalShellClosingExceptionListener(Display display) {
		super(display);
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
		
		//create the screen capture
		super.preException(desc);
		
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "closing modal shells pre thrown exception");
        new ExceptionHandlingHelper(_display, true).closeOpenShells();
	}
}