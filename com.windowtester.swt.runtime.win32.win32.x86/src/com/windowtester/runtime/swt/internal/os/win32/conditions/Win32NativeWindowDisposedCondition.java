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

import com.windowtester.runtime.swt.internal.os.IAccessibleWindow;
import com.windowtester.runtime.swt.internal.os.IWindowService;

public class Win32NativeWindowDisposedCondition extends Win32NativeWindowCondition {
	
	public Win32NativeWindowDisposedCondition(IWindowService windowService, String windowTitle) {
		super(windowService, windowTitle);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean testInUI() {
		IAccessibleWindow[] dialogs = getDialogs();
		for (int i = 0; i < dialogs.length; i++) {
			if (testDialog(dialogs[i]))
				return false;
		}
		return true;
	}

}
