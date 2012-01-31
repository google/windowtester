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
package com.windowtester.runtime.swt.condition;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.condition.ICondition;

/**
 * An adapter for {@link ICondition} that performs a test on the SWT UI thread rather than
 * the test thread so that SWT widgets can be safely accessed. Implement the
 * {@link #testUI(Display)} method rather than {@link #test()}.
 */
public abstract class SWTUIConditionAdapter
	implements ICondition
{
	private Display display;

	/**
	 * Overrides the superclass implementation to call {@link #testUI(Display)} on the SWT
	 * UI thread.
	 */
	public final boolean test() {

		// If this is running on the UI thread already, then just make the call
		display = Display.getCurrent();
		if (display != null)
			return testUI(display);

		// Otherwise execute the call on the UI thread and wait for the result
		display = Display.getDefault();
		final boolean[] result = new boolean[1];
		display.syncExec(new Runnable() {
			public void run() {
				if (testUI(display))
					result[0] = true;
			}
		});
		return result[0];
	}

	/**
	 * Determine if the condition has been satisfied. This is guarenteed to be executed on
	 * the SWT UI thread.
	 * 
	 * @param display the current SWT {@link Display} (not <code>null</code>)
	 * @return <code>true</code> if the condition is satisfied, else <code>false</code>
	 */
	public abstract boolean testUI(Display display);
}