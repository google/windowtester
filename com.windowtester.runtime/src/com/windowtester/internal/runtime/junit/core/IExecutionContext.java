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


import com.windowtester.runtime.IUIContext;

/**
 * Encapsulates execution context variabilities (e.g., swing vs. swt).
 */
public interface IExecutionContext {

	/**
	 * Get the UIContext instance appropriate for this test execution.
	 */
	IUIContext getUI();
	
	
	/**
	 * Get the Execution monitor for monitoring test runs.
	 */
	IExecutionMonitor getExecutionMonitor();
	
}
