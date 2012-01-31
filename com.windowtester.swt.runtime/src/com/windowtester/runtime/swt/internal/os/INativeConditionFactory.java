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
package com.windowtester.runtime.swt.internal.os;

import com.windowtester.runtime.condition.ICondition;

/**
 * Implementers supply platform-specific conditions.
 *
 */
public interface INativeConditionFactory {

	/**
	 * Get a condition that tests whether a native dialog with the 
	 * given title is showing.
	 */
	ICondition nativeDialogShowing(String windowTitle);

	/**
	 * Get a condition that tests whether a native dialog with the 
	 * given title is disposed.
	 */
	ICondition nativeDialogDisposed(String windowTitle);

	/**
	 * Get a condition that tests whether a native message box has the 
	 * given message.
	 */
	ICondition messageBoxMessageText(String msg);
	
	
}
