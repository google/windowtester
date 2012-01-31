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

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.Display;

/**
 * Thread debugging utility.
 */
public class ThreadUtil
{
	private static final Object LOCK = new Object();
	private static Timer timer;
	
	/**
	 * Periodically dump the stack to System.err.
	 * This automatically cancels any previously scheduled stack dumps.
	 */
	public static void startPrintStackTraces(long period) {
		final Thread callingThread = Thread.currentThread();
		synchronized (LOCK) {
			stopPrintStackTraces();
			timer = new Timer("ThreadUtil Stack Dump");
			timer.schedule(new TimerTask() {
				public void run() {
					System.err.println("**********************************************************************************");
					System.err.println("Periodic Thread Dump: " + System.currentTimeMillis());
					System.err.println("Default Display Thread: " + Display.getDefault().getThread());
					System.err.println("Calling Thread: " + callingThread);
					printStackTraces();
				}
			}, period, period);
		}
	}
	
	/**
	 * Stop any scheduled stack dumps
	 */
	public static void stopPrintStackTraces() {
		synchronized (LOCK) {
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
		}
	}
	
	/**
	 * Get a string representation of the current stack state of all the active threads.
	 */
	public static String getStackTraces() {
		StringWriter stringWriter = new StringWriter(5000);
		printStackTraces(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
	
	/**
	 * Print a string representation of the current stack state of all the active threads.
	 */
	public static void printStackTraces() {
		printStackTraces(new PrintWriter(new OutputStreamWriter(System.err)));
	}

	/**
	 * Print a string representation of the current stack state of all the active threads.
	 */
	public static void printStackTraces(PrintWriter writer) {
		Map<Thread, StackTraceElement[]> map;
		try {
			map = Thread.getAllStackTraces();
		}
		catch (Throwable e) {
			writer.println("Failed to obtain stack traces: " + e);
			return;
		}
		if (map == null) {
			writer.println("No stack traces available");
			return;
		}
		for (Entry<Thread, StackTraceElement[]> entry : map.entrySet())
			printStackTrace(writer, entry.getKey(), entry.getValue());
		writer.flush();
	}

	private static void printStackTrace(PrintWriter writer, Thread thread, StackTraceElement[] trace) {
		try {
			writer.println(thread.toString() + ":");
			for (int i = 0; i < trace.length; i++)
				writer.println("\tat " + trace[i]);
		}
		catch (Exception e) {
			writer.println("\t*** Exception printing stack trace: " + e);
		}
		writer.flush();
	}
}
