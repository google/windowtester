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

import com.windowtester.internal.runtime.Platform;

/**
 * Tracing service.
 * 
 */
public class TraceHandler {

	//TODO: we need a proper log handling scheme for the non-platform case
	private static final boolean sendToConsole = false;
	
	
	/**
	 * If trace messages associated with the given trace option have been
	 * enabled, log the given message to the debugging log file.
	 *
	 * @param optionName the name of the trace option used to determine whether
	 *        the trace message should be written.
	 *        Typically the optionName takes the form "plug-in-id/trace-option"
	 * @param message the trace message to be written
	 */
	public static void trace(String optionName, String message) {
		if (Platform.isRunning())
			Tracer.trace(optionName, message);
		else {
			if (sendToConsole) 
				System.out.println("trace[" + optionName + "]: " + message);
		}
	}

}
