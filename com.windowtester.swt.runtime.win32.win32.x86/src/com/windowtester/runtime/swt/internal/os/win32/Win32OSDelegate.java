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
package com.windowtester.runtime.swt.internal.os.win32;

import org.eclipse.actf.accservice.core.win32.msaa.InitializationException;

import com.windowtester.runtime.swt.internal.os.INativeConditionFactory;
import com.windowtester.runtime.swt.internal.os.IOSDelegate;
import com.windowtester.runtime.swt.internal.os.IWindowService;

/**
 * Provides OS services for win32.
 */
public class Win32OSDelegate implements IOSDelegate {

	private IWindowService windowService;
	private INativeConditionFactory conditionFactory;
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.os.IOSDelegate#getWindowService()
	 */
	public IWindowService getWindowService() {
		if (windowService == null)
			windowService = createWindowService();
		return windowService;
	}

	private IWindowService createWindowService() {
		try {
			return new Win32WindowService();
		} catch (InitializationException e) {
			// TODO --- replace w/ null/default object here?
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.os.IOSDelegate#getConditionFactory()
	 */
	public INativeConditionFactory getConditionFactory() {
		if (conditionFactory == null)
			conditionFactory = createConditionFactory();
		return conditionFactory;
	}

	private INativeConditionFactory createConditionFactory() {
		return new Win32ConditionFactory(getWindowService());
	}
	

}
