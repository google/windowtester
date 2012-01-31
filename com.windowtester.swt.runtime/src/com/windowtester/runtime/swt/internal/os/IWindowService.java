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
 * Implementers provide access to native windows.
 *
 */
public interface IWindowService {

	/**
	 * Retrieve the (native) top level window handles from the OS.
	 */
	IAccessibleWindow[] getTopLevelWindows();
	
	/**
	 * Get native dialog windows owned by this process.
	 */
	IAccessibleWindow[] getNativeDialogs();
	
}
