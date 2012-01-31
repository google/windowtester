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
import com.windowtester.runtime.swt.internal.idle.SWTIdler;

/**
 * Tests if the SWT UI thread is idle. Note that this condition would
 * <em>always</em> resolve true when called from the SWT UI thread because it makes no sense for
 * a condition to wait for the SWT UI thread to be idle when executed from the SWT UI
 * thread.
 */
public class SWTIdleCondition
	implements ICondition
{
	
	//The idler does all the heavy lifting
	private final SWTIdler idler;
	
	/**
	 * Construct a new condition to wait for the current SWT display thread to have
	 * processed all events in its event queue.
	 */
	public SWTIdleCondition() {
		this(Display.getDefault());
	}

	/**
	 * Construct a new condition to wait for the specified SWT display thread to have
	 * processed all events in its event queue.
	 */
	public SWTIdleCondition(Display display) {
		idler = SWTIdler.forDisplay(display);
	}

	/**
	 * Determine if the UI thread is idle
	 * 
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		return idler.isIdle();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return " check if SWT UI Thread is idle";
	}

	/**
	 * Use the receiver to wait until the UI thread is idle
	 */
	public void waitForIdle() {
		idler.waitForIdle();
	}
	
}
