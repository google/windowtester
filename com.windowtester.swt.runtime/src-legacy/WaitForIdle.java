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
package com.windowtester.swt.util;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.swt.condition.SWTIdleCondition;

/**
 * The purpose of this object is to wait for the display thread to reach an "idle" state.
 * This must take into account that the display thread may change such as when a dialog
 * opens or closes.
 * @author Dan Rubel
 * @deprecated Use {@link SWTIdleCondition} instead... will be removed sometime after Dec 2008
 */
public class WaitForIdle
{
	/**
	 * This method does not return until the display thread is in an "idle" state.
	 */
	public void waitForIdle() {
		//Use the current default display
		waitForIdle(Display.getDefault());
	}

	/**
	 * This method does not return until the display thread is in an "idle" state.
	 */
	public void waitForIdle(Display display) {
		new SWTIdleCondition(display).waitForIdle();
	}
}
