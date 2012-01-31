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
 * A subinterface of {@link Callable} that returns a type of result of type T and can
 * handle exceptions if they occur.
 */
public interface SafeCallable<T>
	extends Callable<T>
{
	/**
	 * If the {@link #call()} method throws an exception, then this method is called to
	 * handle that exception. This method may either handle the exception and return a
	 * value or rethrow the exception
	 * 
	 * @param e the exception that occurred during execution of {@link #call()}
	 */
	T handleException(Throwable e) throws Throwable;
}
