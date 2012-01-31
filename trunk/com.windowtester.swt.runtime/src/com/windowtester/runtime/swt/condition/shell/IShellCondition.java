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

import org.eclipse.swt.widgets.Shell;

/**
 * An interface used by {@link IShellMonitor} to test whether a condition has been
 * satisfied.
 */
public interface IShellCondition
{
	/**
	 * Determine if the condition has been satisfied.
	 * <p>
	 * Note that this method is guaranteed to be called on the UI thread.
	 * 
	 * @param shell the shell to be tested (not <code>null</code>)
	 * @return <code>true</code> if the condition is satisfied, else <code>false</code>
	 */
	boolean test(Shell shell);
}
