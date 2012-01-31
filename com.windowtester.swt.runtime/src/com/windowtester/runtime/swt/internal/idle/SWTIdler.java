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
package com.windowtester.runtime.swt.internal.idle;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.internal.OS;

/**
 * A strategy object for performing SWT idle operations.
 */
public abstract class SWTIdler {
	
	
	/** Synchronize against this object before accessing isIdle or isChecking or checkStart */
	protected final Object lock = new Object();
	
	protected boolean isIdle;
	protected boolean isChecking;
	protected long checkStart = 0;
	
	private Display display;
	
	/**
	 * Create an idler for the given display. 
	 * @param display
	 * @return
	 * @since 3.8.1
	 */
	public static SWTIdler forDisplay(Display display) {
		return getIdlerForOS().withDisplay(display);

	}
	
	/**
	 * @since 3.8.1
	 */
	private static SWTIdler getIdlerForOS() {
		if (OS.isLinux())
			return new LinuxIdler();
		return new DefaultIdler();
	}
		
	/**
	 * @since 3.8.1
	 */
	private SWTIdler withDisplay(Display display) {
		this.display = display;
		return this;
	}

	/**
	 * @return the display
	 */
	public Display getDisplay() {
		return display;
	}


	/**
	 * @since 3.8.1
	 */
	public abstract boolean isIdle();
	
	/**
	 * @since 3.8.1
	 */
	public abstract void waitForIdle();
	

}
