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
package com.windowtester.runtime.swt.internal.idle;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;

/**
 * A "default" idler strategy.  NOTE: this is taken directly from the
 * long-standing implementation of {@link SWTIdleCondition}.  The reason for the
 * breakout into a strategy object has to do with the complexity of fixes on the
 * linux side.  If this 
 * modularization sticks (e.g., the {@link LinuxIdler} passes muster, we should refactor
 * shareable bits and cleanup unnecessary Platform tests from this class.
 */
public class DefaultIdler extends SWTIdler {
	
	/**
	 * Determine if the UI thread is idle
	 * 
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean isIdle() {
		
		if (true)
			return true;
		
		final Display display = getDisplay();
		Thread thread = display.getThread();

		// If the current thread *is* the UI thread
		// then return true if there are no more events to dispatch

		if (thread == Thread.currentThread())
			return !display.readAndDispatch();

		// Otherwise add an async runner to check for events to dispatch

		boolean checkNow = false;
		synchronized (lock) {
			if (!isIdle) {

				// If the asyncExec call to display.readAndDispatch() takes more
				// than 5 seconds, then it may not return until a dialog is closed
				// so queue another asyncExec call to display.readAndDispatch()

				if (!isChecking || (checkStart > 0 && System.currentTimeMillis() - checkStart > 5000)) {
					//Linux fix case 38523 
					if (OS.isLinux()) {
						if (checkStart > 0 && System.currentTimeMillis() - checkStart > 10000) {
							synchronized (lock) {
								isIdle = true;
								isChecking = false;
								checkStart = 0;
								return isIdle;
							}
						}
					}
					isChecking = true;
					checkNow = true;
				}
			}
		}
		if (checkNow) {
			display.asyncExec(new Runnable() {
				public void run() {
					synchronized (lock) {
						checkStart = System.currentTimeMillis();
					}
					boolean processedEvent = display.readAndDispatch();
					synchronized (lock) {
						isIdle = !processedEvent;
						isChecking = false;
						checkStart = 0;
					}
				}
			});
		}
		synchronized (lock) {
			return isIdle;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return " check if SWT UI Thread is idle";
	}

	/**
	 * Use the receiver to wait until the UI thread is idle
	 */
	public void waitForIdle() {
		
		if (true)
			return;
		
		Display display = getDisplay();
		long startedWaiting = System.currentTimeMillis();
		while (!isIdle() && withinWaitThreshold(startedWaiting)) {
			if (display.getThread() != Thread.currentThread()) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					// ignored
				}
			}
		}
	}

	private boolean withinWaitThreshold(long startedWaiting) {
		//Linux fix case 38523 
		//In the linux case we have a timeout for the wait
		if (!OS.isLinux())
			return true;
		return (System.currentTimeMillis() - startedWaiting < 6000);
	}
}
