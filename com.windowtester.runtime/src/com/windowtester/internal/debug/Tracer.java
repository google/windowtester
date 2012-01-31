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
package com.windowtester.internal.debug;

import java.io.PrintWriter;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

/**
 * The class <code>Tracer</code> provides support for code using the runtime
 * debug tracing facility.
 * <p>
 * @author Brian Wilkerson
 * @author Dan Rubel
 * @version $Revision: 1.4 $
 */
public class Tracer
{
	/**
	 * A print writer wrapping System.out.
	 * Override the close method so that it cannot be closed.
	 */
	private static final PrintWriter sysOut = new PrintWriter(System.out) {
		public void close() {
			flush();
		}
	};

	/**
	 * Flag indicating whether or not debugging is active
	 */
	private static boolean isDebugging = false;

	/**
	 * The log to which trace information is appended
	 */
	private static ILog log;

	////////////////////////////////////////////////////////////////////////////
	//
	// Initialization
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Disallow the creation of instances of this class.
	 */
	private Tracer() {
	}

	/**
	 * Set the log to which trace information is appended.
	 * 
	 * @param log the log (not <code>null</code>)
	 */
	public static void setLog(ILog log) {
		Tracer.log = log;
	}

	/**
	 * Turn tracing on or off.
	 * 
	 * @param isDebugging Flag indicating whether or not debugging is active
	 */
	public static void setDebugging(boolean isDebugging) {
		Tracer.isDebugging = isDebugging;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Accessing
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Return <code>true</code> if debugging is enabled.
	 *
	 * @return <code>true</code> if debugging is enabled
	 */
	public static boolean isDebugging() {
		return isDebugging;
	}

	/**
	 * Return <code>true</code> if debugging is enabled.
	 *
	 * @return <code>true</code> if debugging is enabled
	 */
	public static boolean isTracing(String optionName) {
		if (!isDebugging)
			return false;
		if (optionName == null)
			return true;
		return "true".equalsIgnoreCase(Platform.getDebugOption(optionName));
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Tracing
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * If trace messages associated with the given trace option have been
	 * enabled, log the given message to the debugging log file.
	 *
	 * @param optionName the name of the trace option used to determine whether
	 *        the trace message should be written.
	 *        Typically the optionName takes the form "plug-in-id/trace-option"
	 * @param message the trace message to be written
	 */
	public static void trace(String optionName, String message) {
		trace(optionName, message, null);
	}

	/**
	 * If trace messages associated with the given trace option have been
	 * enabled, log the given message to the debugging log file. If the given
	 * detail object is an exception, the stack trace will be included in the
	 * log. Otherwise, if the detail object is non-<code>null</code>, its print
	 * string will be included in the log.
	 *
	 * @param optionName the name of the trace option used to determine whether
	 *        the trace message should be written.
	 *        Typically the optionName takes the form "plug-in-id/trace-option"
	 * @param message the trace message to be written
	 * @param detail the object used to provide more detail in the log
	 */
	public static void trace(String optionName, String message, Object detail) {
		if (isTracing(optionName)) {
			Thread thread = Thread.currentThread();
			String info = "[" + thread.getName() + "/" + thread.getPriority() + "] " + message;
			Logger.printLog(sysOut, info, detail);
			IStatus status = Logger.createLogStatus(info, detail, "trace");
			try {
				log.log(status);
			}
			catch (Throwable ex) {
				// ignored
			}
		}
	}
	
	/**
     * Causes the currently executing thread to sleep (temporarily cease 
     * execution) for the specified number of milliseconds. The thread 
     * does not lose ownership of any monitors.
     * 
	 * If trace messages associated with the given trace option have been
	 * enabled, log the start and end of the sleep cycle along with thread information.
	 * If trace options are not enabled, then just sleep.
     *
     * @param millis the length of time to sleep in milliseconds.
	 * @param optionName the name of the trace option used to determine whether
	 *        the trace message should be written.
	 *        Typically the optionName takes the form "plug-in-id/trace-option"
	 * @param message the trace message to be written
	 */
	public static void traceSleep(long millis, String optionName, String message) {
		trace(optionName, message, "sleep " + millis);
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) {
			// ignored... fall through
		}
		trace(optionName, message, "sleep end");
	}
}