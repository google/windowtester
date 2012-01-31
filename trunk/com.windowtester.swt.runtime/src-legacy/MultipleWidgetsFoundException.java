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
package com.windowtester.swt;

import com.windowtester.runtime.IAdaptable;


/**
 * Thrown when mutliple widgets are found.
 * @deprecated Use {@link com.windowtester.runtime.MultipleWidgetsFoundException} instead
 */
public class MultipleWidgetsFoundException extends WidgetSearchException implements IAdaptable {

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
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		//fix to adapt legacy exceptions
		if (adapter == com.windowtester.runtime.WidgetSearchException.class) {
			return new com.windowtester.runtime.MultipleWidgetsFoundException(this);
		}
		return null;
	}
	
}
