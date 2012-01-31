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
package com.windowtester.runtime.swt.locator;

import org.eclipse.swt.widgets.MessageBox;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.os.OSDelegate;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates native {@link MessageBox} widgets.
 */
public class MessageBoxLocator {

	
	/**
	 * Create a condition that tests that the currently active message box has the given title.
	 * @param msg the expected msg
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public ICondition hasMessage(String msg) {		
		return OSDelegate.getCurrent().getConditionFactory().messageBoxMessageText(msg);
	}

}
