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
 * A global placeholder (ick) for the current execution context.
 */
public class ExecutionMonitor {

	private static IExecutionContext _execContext;
	
	public static void setContext(IExecutionContext execContext) {
		_execContext = execContext;
	}

	public static IUIContext getUI() {
		return getContext().getUI();
	}
	
	public static IExecutionContext getContext() {
		return _execContext;
	}
	
}
