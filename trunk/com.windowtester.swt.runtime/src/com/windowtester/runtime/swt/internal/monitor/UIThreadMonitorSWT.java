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
package com.windowtester.runtime.swt.internal.monitor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.windowtester.internal.runtime.monitor.UIThreadMonitorCommon;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.monitor.IUIThreadMonitor;

/**
 * Monitors the UI Thread and notifies listeners if the UI thread is either hung or idle
 * for an extended period of time. This is accomplished by launching a background thread
 * (minimum priority) that checks to see if the UI is responsive and processing input. If
 * the UI becomes unresponsive or idle for a period longer than expected, then the
 * associated {@link com.windowtester.runtime.monitor.IUIThreadMonitorListener} (see
 * {@link #setListener(com.windowtester.runtime.monitor.IUIThreadMonitorListener)}) is
 * notified.
 */
public class UIThreadMonitorSWT extends UIThreadMonitorCommon
	implements IUIThreadMonitor
{
	/**
	 * Types of SWT events that should be happening in a UI test.
	 */
	protected static final int[] EVENT_TYPES = {
		SWT.KeyDown, SWT.KeyUp, SWT.HardKeyDown, SWT.HardKeyUp, SWT.MenuDetect, SWT.MouseDown, SWT.MouseUp,
		SWT.MouseMove, SWT.MouseEnter, SWT.MouseExit, SWT.MouseDoubleClick
	};

	/**
	 * The display whose UI thread is to be monitored.
	 */
	private final Display _display;

	/**
	 * An SWT Event listener to notice proper flow of UI events.
	 */
	public final Listener _swtEventListener = new Listener() {
		public void handleEvent(Event event) {
			markEventProcessed();
			trace("event processed", event.type);
		}
	};

	/**
	 * A runnable to notice responsivenes of the UI thread.
	 */
	public final Runnable _threadResponsiveRunnable = new Runnable() {
		public void run() {
			markUIThreadResponsive();
		}
	};

	// //////////////////////////////////////////////////////////////////////////
	//
	// Constructor
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Construct a new instance to monitor the health of the user interface thread.
	 * 
	 * @param uiContext the user interface context (not <code>null</code>)
	 */
	public UIThreadMonitorSWT(IUIContext uiContext, Display display) {
		super(uiContext);
		_display = display;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// IUIThreadMonitor Accessors
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Add event listeners to monitor events being processed by the UI.
	 */
	protected void addEventListeners() {
		_display.syncExec(new Runnable() {
			public void run() {
				for (int i = 0; i < EVENT_TYPES.length; i++) {
					_display.addFilter(EVENT_TYPES[i], _swtEventListener);
				}
			}
		});
	}

	/**
	 * Remove the event listeners added to monitor events being processed by the UI.
	 */
	protected void removeEventListeners() {
		_display.syncExec(new Runnable() {
			public void run() {
				for (int i = 0; i < EVENT_TYPES.length; i++)
					_display.removeFilter(EVENT_TYPES[i], _swtEventListener);
			}
		});
	}

	/**
	 * Determine if the test has ended.
	 * 
	 * @return <code>true</code> if test has ended, else <code>false</code>
	 */
	protected boolean hasTestEnded() {
		return _display.isDisposed() || getListener() == null;
	}

	/**
	 * Wait for up to one second to determine if the user interface thread is responsive
	 * and processing new events.
	 * 
	 * @return <code>true</code> if the user interface thread is responsive, or
	 *         <code>false</code> if it has not processed any new events within the last
	 *         one second
	 */
	protected boolean isUIThreadResponsive() {
		synchronized (_lock) {
			_uiThreadResponsive = false;
		}
		_display.asyncExec(_threadResponsiveRunnable);
		for (int i = 0; i < 100; i++) {
			boolean b;
			b = wasUIThreadResponsive();
			if (b)
				break;
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				// ignored
			}
		}
		synchronized (_lock) {
			return _uiThreadResponsive;
		}
	}

	protected boolean wasUIThreadResponsive() {
		boolean b;
		synchronized (_lock) {
			b = _uiThreadResponsive;
		}
		return b;
	}
}