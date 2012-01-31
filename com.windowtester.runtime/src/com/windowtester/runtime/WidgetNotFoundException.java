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
 * Thrown when a widget is not found.
 */
public class WidgetNotFoundException extends WidgetSearchException {

	private static final long serialVersionUID = -7232101203251666634L;

	/**
	 * Create an instance with no specified detail message.
	 */
	public WidgetNotFoundException() {}
	
	/**
	 * Create an instance with the specified detail message.
	 */
	public WidgetNotFoundException(String msg) { 
		super(msg); 
	}
	/**
	 * Create an instance with the given cause.
	 */
	public WidgetNotFoundException(Throwable cause) {
		super(cause);
	}
}
