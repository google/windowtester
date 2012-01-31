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
package com.windowtester.runtime.swt.locator.eclipse;

import com.windowtester.runtime.condition.IUIConditionHandler;
import com.windowtester.runtime.swt.condition.eclipse.WorkbenchHasFocusCondition;
import com.windowtester.runtime.swt.condition.eclipse.WorkbenchIsMaximizedCondition;

/**
 * Provides access to the Eclipse workbench.
 */
public class WorkbenchLocator {

	/**
	 * Create a condition that tests if the workbench shell has focus.
	 */
	public IUIConditionHandler hasFocus() {
		return new WorkbenchHasFocusCondition();
	}

	/**
	 * Create a condition that tests if the workbench shell is maximized.
	 */
	public IUIConditionHandler isMaximized() {
		return new WorkbenchIsMaximizedCondition();
	}
	
}
