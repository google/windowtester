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
package com.windowtester.runtime.swt.internal.junit;


import com.windowtester.internal.runtime.junit.core.IExecutionContext;
import com.windowtester.internal.runtime.junit.core.IExecutionMonitor;
import com.windowtester.runtime.IUIContext;

/**
 * An execution context for SWT tests.
 */
public class SWTExecutionContext implements IExecutionContext {

	private SWTExecutionMonitor _execMon;


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.context.IExecutionContext#getExecutionMonitor()
	 */
	public IExecutionMonitor getExecutionMonitor() {
		if (_execMon == null)
			_execMon = new SWTExecutionMonitor();
		return _execMon;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.context.IExecutionContext#getUI()
	 */
	public IUIContext getUI() {
		//TODO: does this method belong on execMon?
		return ((SWTExecutionMonitor)getExecutionMonitor()).getUI();
	}

}
