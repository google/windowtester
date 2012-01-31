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
package com.windowtester.runtime.swt.internal.operation;

/**
 * If an {@link SWTOperation.Step} fails to execute normally, then it may throw this
 * exception indicating that the operation may retry executing this step after a brief
 * delay up to the maximum retry period.
 * 
 * @see SWTOperation#execute()
 */
public class SWTOperationStepException extends RuntimeException
{
	private static final long serialVersionUID = -6185863538718177571L;

	/**
	 * Construct a new exception indicating that the currently executing step can be
	 * retried after a brief delay up to the maximum retry period.
	 * 
	 * @see SWTOperation#execute()
	 */
	public SWTOperationStepException() {
		super();
	}

	/**
	 * Construct a new exception indicating that the currently executing step can be
	 * retried after a brief delay up to the maximum retry period.
	 * 
	 * @see SWTOperation#execute()
	 */
	public SWTOperationStepException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct a new exception indicating that the currently executing step can be
	 * retried after a brief delay up to the maximum retry period.
	 * 
	 * @see SWTOperation#execute()
	 */
	public SWTOperationStepException(String message) {
		super(message);
	}

	/**
	 * Construct a new exception indicating that the currently executing step can be
	 * retried after a brief delay up to the maximum retry period.
	 * 
	 * @see SWTOperation#execute()
	 */
	public SWTOperationStepException(Throwable cause) {
		super(cause);
	}
}
