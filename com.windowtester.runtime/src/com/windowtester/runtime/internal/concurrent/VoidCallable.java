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
package com.windowtester.runtime.internal.concurrent;

import java.util.concurrent.Callable;

/**
 * An alternate form of {@link Callable} that does not return any value from its call
 * method. If an except
 */
public abstract class VoidCallable
{
	/**
	 * Performs an operation with no return value.
	 * 
	 * @throws Exception if unable to compute a result
	 */
	public abstract void call() throws Exception;

	/**
	 * If the {@link #call()} method throws an exception, then this method is called to
	 * handle that exception. This method may either handle the exception and return a
	 * value or rethrow the exception. The default implementation just rethrows the
	 * exception. Subclasses may extend or override.
	 * 
	 * @param e the exception that occurred during execution of {@link #call()}
	 */
	public void handleException(Throwable e) throws Throwable {
		throw e;
	}
}
