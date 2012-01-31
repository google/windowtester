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

/**
 * A "null-object" delegate to handle the fall-back case where the OS 
 * is unrecognized.
 *
 */
public class UnsupportedOSDelegate extends InvalidOSDelegate {

	public UnsupportedOSDelegate() {
		this(getOSDescription());
	}

	private static String getOSDescription() {
		return System.getProperty("os.name") + " [v. " + System.getProperty("os.version") + ", " + System.getProperty("os.arch") + "] is not supported";
	}
	
	UnsupportedOSDelegate(String reason) {
		super.reason = reason;
	}
	
	
}
