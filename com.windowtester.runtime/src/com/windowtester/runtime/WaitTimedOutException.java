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
package com.windowtester.runtime;

/**
 * Thrown when a wait condition time limit is exceeded.
 */
public class WaitTimedOutException extends RuntimeException
{
	private static final long serialVersionUID = -8129128536949106306L;

	public WaitTimedOutException(String msg) {
		super(msg);
	}

	public WaitTimedOutException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
