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
package com.windowtester.runtime.swt.internal.condition.shell;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.condition.shell.IShellCondition;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;

/**
 * A bridge between {@link IShellCondition} instances which are processed on the UI thread
 * and {@link ICondition} which are processed on the WindowTester test thread. The
 * receiver uses {@link org.eclipse.swt.widgets.Display#syncExec(java.lang.Runnable)} to
 * process the {@link IShellCondition#test(Shell)} on the UI thread and return the result
 * on the WindowTester thread.
 */
public class ShellConditionAdapter
	implements ICondition
{
	
	/**
	 * A flag to indicate whether we test the active shell to ensure that it is
	 * valid (e.g. that there are no non-active modal shells that should actually
	 * be considered the active shell).
	 */
	//private static final boolean VALIDATE_ACTIVE_SHELL = true;
	
	/**
	 * The shell condition being adapted
	 */
	private final IShellCondition _condition;

	/**
	 * Construct a new instance adapting the specified shell condition to a
	 * {@link ICondition}.
	 * 
	 * @param condition the shell condition to be adapted
	 */
	protected ShellConditionAdapter(IShellCondition condition) {
		if (condition == null)
			throw new IllegalArgumentException("Condition cannot be null");
		_condition = condition;
	}

	/**
	 * Get the adapted shell condition.
	 * 
	 * @return the adapted shell condition
	 */
	public IShellCondition getCondition() {
		return _condition;
	}
	
	
	/**
	 * Determine if an SWT event has occurred that has satisfied the condition. Calling
	 * this method will reset the internal flag so that this method will return
	 * <code>false</code> the next time that it is called unless a new SWT event occurs
	 * that satisfies the condition. Since the condition occurred on the UI thread and
	 * this method is called on the WindowTester test thread, if this method returns
	 * <code>true</code> subclasses are responsible for determining the condition is
	 * indeed still satisfied.
	 * 
	 * @return <code>true</code> if the condition is satisfied, else <code>false</code>
	 * @see com.windowtester.swt.condition.ICondition#test()
	 */
	public boolean test() {

		/*
		 * Plenty of room for optimization here (if necessary) by
		 * reworking ShellConditionAdapter and ShellMonitor to obtain the display and top
		 * level shell once, call syncExec once, then call multiple instances of
		 * IShellCondition from within that syncExec
		 */

		final Display display = Display.getDefault();
		if (display.isDisposed())
			return false;

		final boolean[] result = new boolean[1];
		final Shell shell = ShellFinder.getActiveShell(display);
		display.syncExec(new Runnable() {
			public void run() {
				result[0] = shell != null && !shell.isDisposed() && _condition.test(shell);
			}
		});
		return result[0];
	}

}
