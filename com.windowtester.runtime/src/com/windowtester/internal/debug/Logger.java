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
import java.util.Date;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

/**
 * Provides logging facilities for jLib and its clients.
 * During setup, initialize the log services
 * used by this class to store/display that log content.
 */

public class Logger
{
	
	private static String PRODUCT_ID = "WindowTester Pro";
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
	 * Simple log wrapper appending info to System.out
	 */
	private static ILog sysOutLog = new ILog() {

		public void addLogListener(ILogListener listener) {
			// ignored
		}

		public void removeLogListener(ILogListener listener) {
			// ignored
		}

		// Necessary for proper compilation in Eclipse 2.1
		/* $codepro.preprocessor.if version == 2.1 $ 
		public org.eclipse.core.runtime.Plugin getPlugin() {
			return null;
		}
		 $codepro.preprocessor.endif $ */

		/* $codepro.preprocessor.if version >= 3.0 $ */
		public org.osgi.framework.Bundle getBundle() {
			return null;
		}
		/* $codepro.preprocessor.endif $ */

		public void log(IStatus status) {
			printLogStatus(sysOut, status);
		}
	};

	/**
	 * The log to which trace information is appended
	 */
	private static ILog log = sysOutLog;

	////////////////////////////////////////////////////////////////////////////
	//
	// Initialization
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Disallow the creation of instances of this class.
	 */
	private Logger() {
	}

	/**
	 * Set the log to which trace information is appended.
	 * 
	 * @param log the log (not <code>null</code>)
	 */
	public static void setLog(ILog log) {
		Logger.log = log != null ? log : sysOutLog;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Logging
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Log the specified exception.
	 * @param ex the exception to be logged.
	 */
	public static void log(Throwable ex) {
		log("Unexpected exception", ex);
	}

	/**
	 * Log the specified message and a stack trace
	 * @param aMessage the message to be logged
	 */
	public static void logStackTrace(String aMessage) {
		try {
			throw new RuntimeException(aMessage);
		}
		catch (RuntimeException ex) {
			log(aMessage, ex);
		}
	}

	/**
	 * Log the specified message and object
	 * @param message the message to be logged
	 * @param detail the object to be logged.
	 *      If anObject is an exception, then the stack trace is logged.
	 *      If anObject is not null, then anObject's toString() is logged
	 *          plus (optionally) any reflect information about the object.
	 */
	public static void log(String message, Object detail) {
		if (Tracer.isDebugging())
			printLog(sysOut, message, detail);
		IStatus status = createLogStatus(message, detail, "log");
		try {
			log.log(status);
		}
		catch (Throwable ex) {
			// ignored
		}
	}

	/**
	 * Log the specified message and object
	 * @param aMessage the message to be logged
	 */
	public static void log(String aMessage) {
		// TODO [author=Dan] this really should be a trace thing
		log(aMessage, null);
	}

	/**
	 * Log the specified status object.
	 *
	 * @param status the status object to be logged
	 */
	public static void log(IStatus status) {
		if (Tracer.isDebugging()) {
			printLogStatus(sysOut, status);
		}
		try {
			log.log(status);
		}
		catch (Throwable exception) {
			// ignored
		}
	}

	/**
	 * Log the specified message without printing the log header
	 * @param aMessage the message to be logged
	 * @deprecated use {@link Tracer} instead
	 */
	public static void logNoHeader(String aMessage) {
		// TODO [author=Dan] this really should be a trace thing
		log(aMessage, null);
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Utility
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Create a status object
	 * 
	 * @param message the trace message
	 * @param detail the trace detail such as an exception, or <code>null</code> if none
	 * @param defaultMessage the default message if the message is null
	 * @return the status object (not <code>null</code>)
	 */
	public static IStatus createLogStatus(String message, Object detail, String defaultMessage) {
		String text;
		if (message == null) {
			if (detail instanceof IStatus)
				return (IStatus) detail;
			text = defaultMessage;
		}
		else {
			text = message;
		}
		if (detail instanceof Throwable)
			return new Status(Status.ERROR, PRODUCT_ID, Status.OK, text, (Throwable) detail);
		if (detail instanceof IStatus)
			return new MultiStatus(PRODUCT_ID, Status.OK, new IStatus[]{
				(IStatus) detail
			}, message, null);
		if (detail == null)
			return new Status(Status.INFO, PRODUCT_ID, Status.OK, text, null);
		StringBuffer buf = new StringBuffer(100);
		buf.append(text);
		buf.append(": ");
		try {
			buf.append(detail);
		}
		catch (Throwable e) {
			buf.append(detail.getClass());
		}
		return new Status(Status.INFO, PRODUCT_ID, Status.OK, buf.toString(), null);
	}

	/**
	 * Log the specified message and object
	 * 
	 * @param writer the writer to which information is appended
	 * @param message the message to append
	 * @param detail the detail to log (e.g. exception, status, etc) or <code>null</code> if none
	 */
	public static void printLog(PrintWriter writer, String message, Object detail) {
		try {
			writer.print("=== ");
			writer.print(new Date().toString());
			writer.println(" : ");

			// Special case if detail is a short string
			if (detail instanceof String) {
				String text = (String) detail;
				if (text.length() < 80) {
					printLogMessage(writer, message + " " + text);
					return;
				}
			}

			printLogMessage(writer, message);
			if (detail instanceof Throwable)
				((Throwable) detail).printStackTrace(writer);
			else if (detail instanceof IStatus)
				printLogStatus(writer, (IStatus) detail);
			else
				printLogObject(writer, detail);
		}
		finally {
			writer.close();
		}
	}

	/**
	 * Log the specified status object
	 * @param writer the log writer (not <code>null</code>)
	 * @param status the status object (not <code>null</code>)
	 */
	public static void printLogStatus(PrintWriter writer, IStatus status) {
		writer.println(status.getMessage());
		writer.println(status);
		Throwable exception = status.getException();
		if (exception != null)
			exception.printStackTrace(writer);
		IStatus[] children = status.getChildren();
		if (children == null)
			return;
		for (int i = 0; i < children.length; i++) {
			IStatus eachChild = children[i];
			if (eachChild != null)
				printLogStatus(writer, eachChild);
		}
	}

	/**
	 * Log the specified object
	 * @param writer the log writer (not <code>null</code>)
	 * @param obj the object
	 */
	public static void printLogObject(PrintWriter writer, Object obj) {
		if (obj == null)
			return;
		Class objClass = obj.getClass();
		String objString;
		try {
			objString = obj.toString();
		}
		catch (Throwable ex) {
			objString = "toString() failed: " + ex.toString();
		}
		writer.println("class = " + objClass.getName());
		writer.println(objString);
	}

	/**
	 * Print the message for a log entry
	 * to the specified file
	 * @param writer the log writer (not <code>null</code>)
	 * @param aMessage the message to be logged
	 */
	public static void printLogMessage(PrintWriter writer, String aMessage) {
		if (aMessage != null) {
			writer.println(aMessage);
		}
	}
}