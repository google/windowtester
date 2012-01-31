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
package com.windowtester.recorder.event.meta;

import com.windowtester.recorder.event.IRecorderSemanticEvent;
import com.windowtester.recorder.event.ISemanticEventHandler;


/**
 * Recorder errors are internal errors that are reported to the host workspace
 * for logging.
 */
public class RecorderErrorEvent implements IRecorderSemanticEvent {

	private static final long serialVersionUID = -5661725350112763499L;

	//TODO: move someplace central
    static String NEW_LINE = System.getProperty("line.separator", "\n");
	
	/** The message describing this event 
	 *@serial 
	 */
	private final String _msg;
	
	/** The throwable associated 
	 *@serial 
	 */
	private final Throwable _throwable;

	/**
	 * Create an instance.
	 * @param msg - a message describing the error
	 * @param throwable - the associated throwable
	 */
	public RecorderErrorEvent(String msg, Throwable throwable) {
		_msg = msg;
		_throwable = throwable;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEvent#accept(com.windowtester.recorder.event.ISemanticEventHandler)
	 */
	public void accept(ISemanticEventHandler handler) {
		handler.handleError(this);
	}

	/**
	 * @return Returns the message.
	 */
	public String getMsg() {
		return _msg;
	}
	
	/**
	 * @return Returns the associated throwable.
	 */
	public Throwable getThrowable() {
		return _throwable;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Internal recorder error (" + getMsg() + "):" + NEW_LINE + getThrowable().getMessage();
	}
	
}
