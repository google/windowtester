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
package com.windowtester.runtime.monitor;

/**
 * Monitors the UI Thread and notifies listeners if the UI thread is either hung or idle
 * for an extended period of time. This is accomplished by launching a background thread
 * (minimum priority) that checks to see if the UI is responsive and processing input. If
 * the UI becomes unresponsive or idle for a period longer than expected, then the
 * associated {@link com.windowtester.runtime.monitor.IUIThreadMonitorListener} (see
 * {@link #setListener(IUIThreadMonitorListener)}) is notified.
 */
public interface IUIThreadMonitor
{
	/**
	 * Set the listener to be notified if the user interface thread becomes idle or
	 * unresponsive for a period longer than expected
	 * 
	 * @param newListener the listener or <code>null</code> if no listener
	 */
	void setListener(IUIThreadMonitorListener newListener);

	/**
	 * Called to indicate that the UI thread may be idle or unresponsive from this moment
	 * for some specified number of milliseconds due some long running operation either on
	 * a background thread or in the UI thread itself.
	 * 
	 * @param millis the expected delay in milliseconds from this point in time
	 */
	void expectDelay(long millis);

	/**
	 * Set the minimum expected delay between user interface events. If the user interface
	 * thread has not processed an event in the specified number of milliseconds, then it
	 * is considered either idle or unresponsive too long and an event is triggered.
	 * 
	 * @param millis the expected delay in milliseconds
	 */
	void setDefaultExpectedDelay(long millis);
}