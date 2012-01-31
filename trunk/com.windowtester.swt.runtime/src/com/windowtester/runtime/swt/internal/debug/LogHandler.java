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
package com.windowtester.runtime.swt.internal.debug;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.runtime.Platform;

/**
 * Logging service.
 * 
 */
public class LogHandler {
	
	private static final boolean sendToConsole = false;

	
	/**
	 * Log the specified exception.
	 * @param ex the exception to be logged.
	 */
	public static void log(Throwable e) {
		if (Platform.isRunning())
			Logger.log(e);
		else {
			if (sendToConsole)
				e.printStackTrace();
		}
	}

	/**
	 * Log the specified message and object
	 * @param aMessage the message to be logged
	 */
	public static void log(String message) {
		if (Platform.isRunning())
			Logger.log(message);
		else {
			if (sendToConsole)
				System.out.println(message);
		}
	}

}
