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
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.debug.ThreadUtil;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.internal.concurrent.SafeCallable;

/**
 * The object responsible for executing {@link Callable} on the SWT UI thread. Rather than
 * calling this class directly, it is preferred to call
 * {@link DisplayReference#execute(Callable, long)}
 * @param <T>
 */
class SWTUIExecutor<T>
{
	/**
	 * Internal lock used when accessing {@link #executing} and {@link #exception}
	 */
	private static final Object LOCK = new Object();

	/**
	 * The callable that the receiver executes on the SWT UI thread.
	 */
	private final Callable<T> callable;

	/**
	 * Indicates whether the callable is executing
	 */
	private boolean executing = false;

	/**
	 * The value returned by the {@link Callable#call()} method.
	 */
	private T result;

	/**
	 * An exception that occurred when executing the {@link Callable#call()} method or
	 * <code>null</code> if none
	 */
	private Throwable exception;

	/**
	 * Construct a new instance for executing the specified {@link Callable} on the SWT UI
	 * thread.
	 * 
	 * @param callable the callable (not <code>null</code>)
	 */
	SWTUIExecutor(Callable<T> callable) {
		if (callable == null)
			throw new IllegalArgumentException();
		this.callable = callable;
	}

	/**
	 * Called on any thread to execute the callable's {@link #runInUI()} method on the SWT
	 * UI thread. Do not call this method directly, but rather
	 * {@link SWTUI#execute(Callable, Display, long)}.
	 * 
	 * @param maxWaitTime the maximum number of milliseconds to wait for the SWT UI thread
	 *            to execute the callable. This value is ignored if this method is called
	 *            on the SWT UI thread.
	 * @param interval the number of milliseconds to sleep before again checking to see if
	 *            the callable has finished executing.
	 * @throws SWTUIException if there is an exception when executing the callable on
	 *             the SWT UI thread
	 * @throws WaitTimedOutException if the SWT UI thread does not execute the callable
	 *             with specified number of milliseconds, but never thrown if this method
	 *             is called on the SWT UI thread.
	 */
	T run(Display display, long maxWaitTime, int interval) {

		// Initialize the execution fields

		synchronized (LOCK) {
			if (executing)
				throw new IllegalStateException("Callable is already executing");
			executing = true;
			exception = null;
		}

		// Execute the callable on the UI thread

		if (Thread.currentThread() == display.getThread())
			execute();
		else
			display.asyncExec(new Runnable() {
				public void run() {
					execute();
				}
			});

		// Wait for the result, exception, or timeout

		long startTime = System.currentTimeMillis();
		while (true) {
			synchronized (LOCK) {
				if (exception != null)
					throw new SWTUIException(exception);
				if (!executing)
					return result;
			}
			long deltaTime = System.currentTimeMillis() - startTime;
			if (deltaTime > maxWaitTime) {
				String errMsg = "Quit waiting for UI thread to execute callable " + System.currentTimeMillis()
					+ "\nElapse time: " + deltaTime + "\nMax wait time: " + maxWaitTime;
				System.err.println(errMsg);
				System.err.println("UI Thread: " + display.getThread());
				System.err.println("This Thread: " + Thread.currentThread());
				ThreadUtil.printStackTraces();
				throw new WaitTimedOutException(errMsg);
			}
			try {
				Thread.sleep(interval);
			}
			catch (InterruptedException e) {
				// ignored... fall through
			}
		}
	}

	/**
	 * Call the callable's {@link Callable#call()} and capture the return value. If an
	 * exception occurs, and the callable implements {@link SafeCallable} then call the
	 * callable's {@link SafeCallable#handleException(Exception)} with the exception that
	 * occurred.
	 */
	private void execute() {
		try {
			result = callable.call();
		}
		catch (Exception ex) {
			if (callable instanceof SafeCallable<?>) {
				try {
					result = ((SafeCallable<T>) callable).handleException(ex);
				}
				catch (Throwable ex2) {
					synchronized (LOCK) {
						exception = ex2;
					}
				}
			}
			else {
				synchronized (LOCK) {
					exception = ex;
				}
			}
		}
		finally {
			synchronized (LOCK) {
				executing = false;
			}
		}
	}
}
