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
 * is invalid or unrecognized.
 */
public class InvalidOSDelegate implements IOSDelegate {

	protected String reason = "<unspecified>";

	public static InvalidOSDelegate forReason(String reason) {
		InvalidOSDelegate delegate = new InvalidOSDelegate();
		delegate.reason = reason;
		return delegate;
	}
	
	/*
	 * @see com.windowtester.runtime.swt.internal.os.IOSDelegate#getConditionFactory()
	 */
	public INativeConditionFactory getConditionFactory() {
		invalid();
		return null;
	}

	/**
	 * Subclasses should implement this to provide specific behavior to
	 * handle the invalid state.  (For example, throwing an exception.)
	 */
	protected void invalid() {
		throw new IllegalStateException("Native services are not supported (Invalid OS Delegate)");
	}

	/** Subclasses may override.
	 * @see com.windowtester.runtime.swt.internal.os.IOSDelegate#getWindowService()
	 */
	public IWindowService getWindowService() {
		invalid();
		return null;
	}

	/**
	 * Get a description of the reason this OS delegate is invalid.
	 */
	public String getReason() {
		return reason;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "InvalidOSDelegate: reason = " + getReason(); 
	}
	
}
