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
package com.windowtester.internal.swing.monitor;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

import javax.swing.SwingUtilities;

import com.windowtester.internal.runtime.monitor.UIThreadMonitorCommon;
import com.windowtester.runtime.IUIContext;

/**
 * Monitors the UI Thread and notifies listeners if the UI thread is either hung or idle
 * for an extended period of time. This is accomplished by launching a background thread
 * (minimum priority) that checks to see if the UI is responsive and processing input. If
 * the UI becomes unresponsive or idle for a period longer than expected, then the
 * associated {@link com.windowtester.runtime.monitor.IUIThreadMonitorListener} (see
 * {@link #setListener(com.windowtester.runtime.monitor.IUIThreadMonitorListener)}) is
 * notified.
 */
public class UIThreadMonitorSwing extends UIThreadMonitorCommon {

	
	/** 
	 *  The types of AWT events that should occur during a test
	 */
	protected static final long EVENT_TYPES = AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK;
	
	
	/**
	 *  An AWT Event Listener
	 */
	public final AWTEventListener _awtEventListener = new AWTEventListener(){
		public void eventDispatched(AWTEvent event) {
			markEventProcessed();
			trace("event processed", event.getID());
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

	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Constructor
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Construct a new instance to monitor the health of the user interface thread.
	 * 
	 * @param uiContext the user interface context (not <code>null</code>)
	 */
	public UIThreadMonitorSwing(IUIContext uiContext) {
		super(uiContext);
	
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// IUIThreadMonitor Accessors
	//
	// //////////////////////////////////////////////////////////////////////////

	
	
	/**
	 * Add event listeners to monitor events being processed by the UI.
	 */
	protected void addEventListeners() {
		Toolkit.getDefaultToolkit().addAWTEventListener(_awtEventListener, EVENT_TYPES);
	}
	
	
	/**
	 * Remove the event listeners added to monitor events being processed by the UI.
	 */
	protected void removeEventListeners() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(_awtEventListener);

	}

	
	
	/**
	 * Determine if the test has ended.
	 * 
	 * @return <code>true</code> if test has ended, else <code>false</code>
	 */
	protected boolean hasTestEnded() {
		return getListener() == null;
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
		SwingUtilities.invokeLater(_threadResponsiveRunnable);
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
