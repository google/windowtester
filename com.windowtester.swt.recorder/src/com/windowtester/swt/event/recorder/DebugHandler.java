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
package com.windowtester.swt.event.recorder;

import com.windowtester.recorder.IEventRecorder;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;

/**
 * A service for handling recorder debugging and tracing.
 */
public class DebugHandler {

	/**
	 * Log the specified message and a stack trace
	 * @param aMessage the message to be logged
	 */
	 public static void log(String msg, Throwable t) {
	 	IEventRecorder recorder = EventRecorderPlugin.getDefault().getRecorder();
	 	recorder.reportError(new RecorderErrorEvent(msg, t));
	 }

	/**
	 * Send this trace message to the tracer
	 * @param optionName the name of the trace option used to determine whether
	 *        the trace message should be written.
	 *        Typically the optionName takes the form "plug-in-id/trace-option"
	 * @param message the trace message to be written
	 */
	public static void trace(String traceOption, String msg) {
	 	IEventRecorder recorder = EventRecorderPlugin.getDefault().getRecorder();
	 	recorder.trace(new RecorderTraceEvent(traceOption, msg));
	}
	
}
