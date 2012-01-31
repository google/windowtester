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
package com.windowtester.internal.runtime.junit.core;

import java.lang.reflect.InvocationTargetException;

/**
 * A cache for exceptions caught in test execution.
 */
public class TestExceptionCache {

	// Cached exceptions for re-throwing
	private InvocationTargetException _ite;
	private IllegalAccessException _iae;
	
	/**
	 * Check if there is an exception cached.
	 * @return <code>true</code> if there is a cached exception, <code>false</code> otherwise
	 */
	public boolean hasException() {
		return _ite != null || _iae != null;
	}

	/**
	 * Cache the given exception for later throwing.
	 */
	public void cache(Throwable e) {
		if (e instanceof InvocationTargetException) {
			// e.printStackTrace();
			e.fillInStackTrace();
			_ite = (InvocationTargetException)e;
		} else if (e instanceof IllegalAccessException) {
			// e.printStackTrace();
			e.fillInStackTrace();
			_iae = (IllegalAccessException)e;
		} else {
			// e.printStackTrace();
			//e.fillInStackTrace();
			_ite = new InvocationTargetException(e);
		}
	}

	/**
	 * Throw the cached exception (if there is one).
	 */
	public void throwException() throws Throwable {
		if (_ite != null) {
			// Extract the wrappered exception as appropriate
			if (_ite.getCause() != null)
				throw _ite.getCause();			
			throw _ite;
		}
		if (_iae != null)
			throw _iae;
	}
	
	
	/**
	 * Clear the exception cache.
	 */
	public void clear() {
		_iae = null;
		_ite = null;
	}

}
