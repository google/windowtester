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
package com.windowtester.runtime.swt.internal.selector;

import com.windowtester.runtime.WT;

/**
 * 
 * The thought is that modified special keys (such as WT.SHIFT|WT.HOME) need a pause before the special key to get registered.
 * 
 * TODO: this handling occurs before every special key press --- this could be optimized to be only on modified ones.
 * 
 *
 * @author Phil Quitslund
 */
public class SpecialKeyHandler {

	
	static final int[] SPECIAL_KEYS = new int[]{WT.END, WT.HOME, WT.ARROW_DOWN, WT.ARROW_UP, WT.ARROW_LEFT, WT.ARROW_RIGHT};
	
	private static final int SPECIAL_KEY_PAUSE = 200;

	/**
	 */
	public static void preDown(int keyCode) {
		if (isSpecialKey(keyCode))
			pause();
	}
	
	public static void preUp(int keyCode) {
		if (isSpecialKey(keyCode))
			pause();
	}
	
	
	private static void pause() {
		DisplayEventDispatcher.pause(SPECIAL_KEY_PAUSE);
	}

	private static boolean isSpecialKey(int keyCode) {
		for (int i = 0; i < SPECIAL_KEYS.length; i++) {
			if (SPECIAL_KEYS[i] == keyCode)
				return true;
		}
		return false;
	}


	
}
