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
 * Base type of all widget search exceptions.
 */
public class WidgetSearchException extends Exception {

	private static final long serialVersionUID = 450468979445524082L;
	
	/**
	 * Create an instance with the specified detail message.
	 */
	public WidgetSearchException(String msg) {
		super(msg);
	}
	/**
	 * Create an instance with no specified detail message.
	 */
	public WidgetSearchException() {}
	/**
	 * Create an instance with the given cause.
	 */
	public WidgetSearchException(Throwable cause) {
		super(cause);
	}


}
