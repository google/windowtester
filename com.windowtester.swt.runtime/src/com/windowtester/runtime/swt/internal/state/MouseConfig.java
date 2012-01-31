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
package com.windowtester.runtime.swt.internal.state;

import org.eclipse.swt.SWT;

import com.windowtester.internal.runtime.Platform;
import com.windowtester.runtime.swt.internal.RuntimePlugin;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;

/**
 * 
 * Manages mouse configuration information.
 * 
 */
public class MouseConfig {

	public static final int BUTTON_MASK = (	SWT.BUTTON1 |
			SWT.BUTTON2 |
			SWT.BUTTON3);
	
	/**
	 * Constant that specifies whether primary and secondary mouse buttons have been swapped.
	 */
	public static final boolean BUTTONS_REMAPPED = getPlaybackSettings().getMouseButtonsRemapped();

	//report setting to the log
	static {
		LogHandler.log("Mouse buttons remapped: " + BUTTONS_REMAPPED);
	}
	
	/**
	 * Constant that identifies the user specified primary mouse button.
	 */
	public static final int PRIMARY_BUTTON = BUTTONS_REMAPPED ? 3 : 1;
	
	/**
	 * Constant that identifies the user specified secondary mouse button.
	 */
	public static final int SECONDARY_BUTTON = BUTTONS_REMAPPED ? 1 : 3;
	
		
	//TODO: should be moved elsewhere (somewhere central)	
	private static PlaybackSettings getPlaybackSettings() {
		return Platform.isRunning() ? RuntimePlugin.getDefault().getPlaybackSettings() : PlaybackSettings.loadFromFile();
	}	
		
	/**
	 * Given a mouse accelerator, extract the button value.  For use in synthesizing
	 * raw events.
	 */
	public static final int getButton(int accelerator) {
		accelerator &= BUTTON_MASK;
		if((accelerator&SWT.BUTTON1)==SWT.BUTTON1)
			return MouseConfig.PRIMARY_BUTTON;
		if((accelerator&SWT.BUTTON2)==SWT.BUTTON2)
			return 2;
		if((accelerator&SWT.BUTTON3)==SWT.BUTTON3)
			return MouseConfig.SECONDARY_BUTTON;
		//is this an error?
		return 0;
	}
}
