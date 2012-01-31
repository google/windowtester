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

import org.eclipse.actf.accservice.core.AccessibleConstants;

import com.windowtester.runtime.swt.internal.os.IAccessibleComponent;
import com.windowtester.runtime.swt.internal.os.IAccessibleWindow;
import com.windowtester.runtime.swt.internal.os.IWindowService;
import com.windowtester.runtime.swt.internal.os.InvalidComponentException;
import com.windowtester.runtime.swt.internal.os.win32.IAccessibleComponentMatcher;
import com.windowtester.runtime.swt.internal.os.win32.MsaaAccessibleHelper;
import com.windowtester.runtime.util.StringComparator;

public class Win32MessageBoxTextCondition extends Win32NativeWindowCondition {

	private final String msg;

	public Win32MessageBoxTextCondition(IWindowService windowService, String msg) {
		super(windowService, ".*" /* ignored */);
		this.msg = msg;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean testInUI() {
		IAccessibleWindow[] dialogs = getDialogs();
		for (int i = 0; i < dialogs.length; i++) {
			if (testDialog(dialogs[i]))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.os.win32.conditions.Win32NativeWindowCondition#testDialog(com.windowtester.runtime.swt.internal.os.IAccessibleWindow)
	 */
	protected boolean testDialog(IAccessibleWindow window) {
		IAccessibleComponent match = MsaaAccessibleHelper.findIn(new IAccessibleComponentMatcher() {
			public boolean matches(IAccessibleComponent element) {
				try {
					if (!isLabel(element))
						return false;
					return hasMatchingName(element);
				} catch (InvalidComponentException e) {
					return false;
				}
			}

		}, window);
		
		return match != null;
	}
	
	
	
	private boolean hasMatchingName(IAccessibleComponent element) throws InvalidComponentException {
		String name = element.getAccessibleName();
		return StringComparator.matches(name, msg);
	}

	private boolean isLabel(IAccessibleComponent element)
			throws InvalidComponentException {
		String role = element.getAccessibleRole();
		if (role == null)
			return false;
		return AccessibleConstants.ROLE_LABEL.equals(role);
	}

}
