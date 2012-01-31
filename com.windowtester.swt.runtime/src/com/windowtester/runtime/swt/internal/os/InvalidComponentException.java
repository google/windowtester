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
package com.windowtester.runtime.swt.internal.os;

public class InvalidComponentException extends Exception {

	private static final long serialVersionUID = -1826552272121309441L;

	public InvalidComponentException () {
		super();
	}

	public InvalidComponentException (String message) {
		super(message);
	}

	public InvalidComponentException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidComponentException (Throwable cause) {
		super(cause);
	}
}