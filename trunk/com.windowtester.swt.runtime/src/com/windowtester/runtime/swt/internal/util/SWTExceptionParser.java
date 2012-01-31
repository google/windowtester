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
package com.windowtester.runtime.swt.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;

/**
 * A utility function for inspecting SWT exceptions.
 */
public class SWTExceptionParser {

	public static boolean isDisposed(SWTException e) {	
		if (testDisposed(e))
			return true;
		if (e.throwable != null)
			return isDisposed(e.throwable);
		return false;
	}

	public static boolean isDisposed(Throwable t) {
		if (t instanceof SWTException)
			return isDisposed((SWTException)t);
		return false;
	}

	private static boolean testDisposed(SWTException e) {
		return e.code == SWT.ERROR_WIDGET_DISPOSED;
	}

}
