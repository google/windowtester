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
 * Thrown when a widget is not found.
 *
 * @deprecated Use {@link com.windowtester.runtime.WidgetSearchException} instead
 */
public class WidgetNotFoundException extends WidgetSearchException implements IAdaptable {

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

	public WidgetNotFoundException(Exception e) {
		super(e);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		//fix to adapt legacy exceptions
		if (adapter == com.windowtester.runtime.WidgetSearchException.class) {
			return new com.windowtester.runtime.WidgetNotFoundException(this);
		}
		return null;
	}
}
