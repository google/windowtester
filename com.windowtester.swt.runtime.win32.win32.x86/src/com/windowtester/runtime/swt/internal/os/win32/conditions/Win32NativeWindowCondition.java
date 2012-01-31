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
package com.windowtester.runtime.swt.internal.os.win32.conditions;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.os.IAccessibleWindow;
import com.windowtester.runtime.swt.internal.os.IWindowService;
import com.windowtester.runtime.swt.internal.os.InvalidComponentException;
import com.windowtester.runtime.util.StringComparator;

public abstract class Win32NativeWindowCondition implements ICondition {
	
	private final String windowTitle;
	private final IWindowService windowService;

	public Win32NativeWindowCondition(IWindowService windowService, String windowTitle) {
		this.windowService = windowService;
		this.windowTitle = windowTitle;
	}

	public final boolean test() {
		final boolean[] result = new boolean[1];
		
		// [Dan] I'm getting hard JVM crashes when calling MsaaWindowService functions
		// so I'm wrappering these methods in syncExec
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				result[0] = testInUI();
			}
		});
		
		return result[0];
	}
		
	protected abstract boolean testInUI();

	protected boolean nameMatchesExpected(String name) {
		return StringComparator.matches(name, windowTitle);
	}

	protected IAccessibleWindow[] getDialogs() {
		return windowService.getNativeDialogs();
	}
	
	protected boolean testDialog(IAccessibleWindow window) {
		try {
			return nameMatchesExpected(window.getAccessibleName());
		} catch (InvalidComponentException e) {
			return false;
		}
	}
	
}
