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
package com.windowtester.runtime.swt.internal.abbot;

/**
 * A Listener that is informed BEFORE exceptions are thrown.  This is 
 * necessary because thrown exceptions block the UI thread.  In order
 * to close windows and otherwise cleanup in the event of a WidgetNotFound
 * Exception, we need to do it BEFORE the exception is thrown.
 */
public interface IExceptionListener {
	
	/**
	 * Called before an exception is thrown.
	 * @param desc - a description of the error (for use in screen capture)
	 */
	public void preException(String desc);
	
}