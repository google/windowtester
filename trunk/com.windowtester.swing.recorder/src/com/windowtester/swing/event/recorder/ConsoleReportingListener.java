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
package com.windowtester.swing.event.recorder;

import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;


public class ConsoleReportingListener implements ISemanticEventListener {

	/** A describer used for stateful event descrptions */
	//private EventDescriber _describer = new EventDescriber();
	
    /* (non-Javadoc)
     * @see com.windowtester.swt.event.model.ISemanticEventListener#notify(com.windowtester.swt.event.model.ISWTSemanticEvent)
     */
    public void notify(IUISemanticEvent event) {
//        String description = _describer.describe(event);
//        if (description != null)
//            System.out.println(description);
        System.out.println("got event" + event);
    }
    
    /**
     * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyAssertionHookAdded(java.lang.String)
     */
    public void notifyAssertionHookAdded(String hookName) {
        System.out.println("hook added: " + hookName);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStart()
     */
    public void notifyStart() {
        System.out.println("recording started");
    }
    /* (non-Javadoc)
     * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStop()
     */
    public void notifyStop() {
        System.out.println("recording stopped");
    }

    /**
     * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyPause()
     */
    public void notifyPause() {
    	 System.out.println("recording paused");
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyWrite()
     */
    public void notifyWrite() {
        System.out.println("recording written");
    }

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyDispose()
	 */
	public void notifyDispose() {
		 System.out.println("display disposed");
	}
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyRestart()
	 */
	public void notifyRestart() {
		System.out.println("recording restarted");
	}

	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyError(com.windowtester.swt.event.model.RecorderErrorEvent)
	 */
	public void notifyError(RecorderErrorEvent event) {
		System.out.println("an internal error occured: " + event);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyTrace(com.windowtester.swt.event.model.RecorderTraceEvent)
	 */
	public void notifyTrace(RecorderTraceEvent event) {
		System.out.println("a trace event was sent: " + event);
	}

	public void notifyControllerStart(int port) {
		System.out.println("controller started on: " + port);
	}

	public void notifyDisplayNotFound() {
		System.out.println("display not found");
	}

	public void notifySpyModeToggle() {
		System.out.println("spy mode toggled");
	}
	
}
