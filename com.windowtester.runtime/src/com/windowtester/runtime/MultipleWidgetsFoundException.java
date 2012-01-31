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
 * Thrown when mutliple widgets are found.
 */
public class MultipleWidgetsFoundException extends WidgetSearchException {

	private static final long serialVersionUID = 4381140981836391058L;

	/**
	 * Create an instance with no specified detail message.
	 */
	public MultipleWidgetsFoundException() {
	}

	/**
	 * Create an instance with the specified detail message.
	 */
	public MultipleWidgetsFoundException(String msg) {
		super(msg);
	}

	/**
	 * Create an instance with the given cause.
	 */
	public MultipleWidgetsFoundException(Throwable cause) {
		super(cause);
	}
	
}
