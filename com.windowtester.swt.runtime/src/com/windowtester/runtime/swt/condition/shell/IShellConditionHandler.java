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
package com.windowtester.runtime.swt.condition.shell;

import com.windowtester.runtime.condition.IHandler;

/**
 * An interface used by {@link IShellMonitor} to test
 * to test and handle a shell condition.
 * <p>
 * 
 * @see IShellCondition
 * @see IHandler
 */
public interface IShellConditionHandler
	extends IShellCondition, IHandler
{
}
