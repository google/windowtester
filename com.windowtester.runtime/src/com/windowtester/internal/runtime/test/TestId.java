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
package com.windowtester.internal.runtime.test;



/**
 * Facade for accessing/creating Test ids.
 */
public class TestId {

	private static final NoRunningTestId NONE = new NoRunningTestId();
	private static final UnknownTestId UNKNOWN = new UnknownTestId();
	
	/**
	 * Not a singleton strictly but a convenience accessor to a single instance.
	 */
	public static UnknownTestId unknown() {
		return UNKNOWN;
	}
	
	/**
	 * Not a singleton strictly but a convenience accessor to a single instance.
	 */
	public static NoRunningTestId none() {
		return NONE;
	}
	
	
}
