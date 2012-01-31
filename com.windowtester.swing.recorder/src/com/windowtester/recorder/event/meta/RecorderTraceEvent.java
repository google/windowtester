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
 * Events that correspond to tracing messages.
 */
public class RecorderTraceEvent implements IRecorderSemanticEvent {

	private static final long serialVersionUID = -747178880240708873L;

	/** The associated trace option.
	 * @serial 
	 */
	private final String _traceOption;
	
	/** The associated trace message.
	 * @serial 
	 */	
	private final String _msg;

	/**
	 * Create an instance.
	 * @param traceOption
	 * @param msg
	 */
	public RecorderTraceEvent(String traceOption, String msg) {
		_traceOption = traceOption;
		_msg = msg;
	}

	/**
	 * @return Returns the msg.
	 */
	public String getMsg() {
		return _msg;
	}
	
	/**
	 * @return Returns the traceOption.
	 */
	public String getTraceOption() {
		return _traceOption;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.ISemanticEvent#accept(com.windowtester.recorder.event.ISemanticEventHandler)
	 */
	public void accept(ISemanticEventHandler handler) {
		handler.handleTrace(this);
	}

}
