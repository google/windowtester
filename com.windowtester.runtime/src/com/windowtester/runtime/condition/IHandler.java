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
package com.windowtester.runtime.condition;

import com.windowtester.runtime.IUIContext;

/**
 * An interface used by {@link IConditionMonitor} to signal an object that the associated
 * condition has been satisfied.
 */
public interface IHandler
{
	/**
	 * Called when the associated condition has been satisfied.
	 * <p>
	 * Note that this method is guaranteed to be executed on the WindowTester thread.
	 * 
	 * @param ui the current UIcontext
	 */
	void handle(IUIContext ui) throws Exception;
}
