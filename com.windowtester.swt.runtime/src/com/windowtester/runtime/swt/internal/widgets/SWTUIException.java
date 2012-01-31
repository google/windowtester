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
package com.windowtester.runtime.swt.internal.widgets;

/**
 * Thrown by {@link SWTUIExecutor} when an exception occurs on the UI thread
 * and propagated to the test thread.
 */
public class SWTUIException extends RuntimeException
{
	private static final long serialVersionUID = 4010871417952168382L;

	public SWTUIException(Throwable cause) {
		this("Exception occurred on the SWT UI Thread", cause);
	}

	public SWTUIException(String message, Throwable cause) {
		super(message, cause);
	}
}
