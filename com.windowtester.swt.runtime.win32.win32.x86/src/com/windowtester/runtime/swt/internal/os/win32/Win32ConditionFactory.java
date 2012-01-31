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

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.os.INativeConditionFactory;
import com.windowtester.runtime.swt.internal.os.IWindowService;
import com.windowtester.runtime.swt.internal.os.win32.conditions.Win32MessageBoxTextCondition;
import com.windowtester.runtime.swt.internal.os.win32.conditions.Win32NativeWindowDisposedCondition;
import com.windowtester.runtime.swt.internal.os.win32.conditions.Win32NativeWindowShowingCondition;

public class Win32ConditionFactory implements INativeConditionFactory {

	private final IWindowService windowService;

	public Win32ConditionFactory(IWindowService windowService) {
		this.windowService = windowService;
	}

	public ICondition nativeDialogDisposed(String windowTitle) {
		return new Win32NativeWindowDisposedCondition(windowService, windowTitle);
	}

	public ICondition nativeDialogShowing(String windowTitle) {
		return new Win32NativeWindowShowingCondition(windowService, windowTitle);
	}

	
	public ICondition messageBoxMessageText(String msg) {
		return new Win32MessageBoxTextCondition(windowService, msg);
	}
	
}
