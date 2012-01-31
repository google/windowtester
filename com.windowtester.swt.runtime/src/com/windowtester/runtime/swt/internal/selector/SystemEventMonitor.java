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
package com.windowtester.runtime.swt.internal.selector;


import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.swt.internal.debug.LogHandler;

public abstract class SystemEventMonitor implements Listener, Runnable {

	private boolean posted = false;
	private int eventType;
	private Widget widget;
	/**
	 * The number of milliseconds to sleep after posting to the OS event queue before
	 * attempting to post again
	 */
	public static final int OS_POST_FAILED_SLEEP_INTERVAL = 100;
	/**
	 * The maximum number of attempts to post an event to the OS event queue
	 */
	public static final int OS_POST_MAX_RETRIES = 30;
	
	public SystemEventMonitor(Widget widget, int eventType){
		this.widget = widget;
		this.eventType = eventType;
	}
	
	public void handleEvent(Event event) {
		if(event.type==eventType)
			posted = true;
	}

	public void run() {
		int tries = 0;
		
		//cache the Display --- NOTE: fetching it later might not be safe if the Widget is disposed
		final Display display = widget.getDisplay();
		
		// add listener to the widget
		display.syncExec(new Runnable() {
			public void run() {
				widget.addListener(eventType, SystemEventMonitor.this);
			}
		});
		try {
			posted = false;
			// run an execution method
			syncExecEvents();
			// sleep until posted == true or timeout
			while (!posted && ++tries <= SystemEventMonitor.OS_POST_MAX_RETRIES) {
				DisplayEventDispatcher.pause(SystemEventMonitor.OS_POST_FAILED_SLEEP_INTERVAL);
			}
		}finally{
			//need to guard against disposed widgets
			if (!widget.isDisposed()) {
				Runnable runnable = new Runnable() {
					public void run() {
						if (!widget.isDisposed())
							widget.removeListener(eventType,
									SystemEventMonitor.this);
					}
				};
				
				//Linux fix case 38523
				if (OS.isLinux())
					display.asyncExec(runnable);
				else
					display.syncExec(runnable);
				
			}
		}
		// keep heavy method out of the cycle
		if (tries > SystemEventMonitor.OS_POST_MAX_RETRIES) 
			LogHandler.log("Dispatched Event Monitor: max retries (" + SystemEventMonitor.OS_POST_MAX_RETRIES + ") exceeded, giving up on waiting for notification.");
	}
	
	public abstract void syncExecEvents();

}
