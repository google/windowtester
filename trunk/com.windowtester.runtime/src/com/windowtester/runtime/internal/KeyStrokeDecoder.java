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
package com.windowtester.runtime.internal;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.runtime.WT;

/**
 * Compound keystroke decoder utility.
 */
public class KeyStrokeDecoder {

	
	//NOTE: made public for testing
	//TODO: this should really have them all!
	//keys
	public static int[] KEY_CONSTANTS = { 
		WT.ARROW_DOWN, WT.ARROW_UP, WT.ARROW_LEFT, WT.ARROW_RIGHT, 
		WT.ESC, WT.TAB, WT.CR, 
		WT.F1, WT.F2, WT.F3, WT.F4,WT.F5, WT.F6, WT.F7, WT.F8, WT.F9, WT.F10, WT.F11, WT.F12, WT.F13, WT.F14, WT.F15,
		WT.HELP, WT.HOME, WT.INSERT,
		WT.PAGE_DOWN, WT.PAGE_UP, WT.PRINT_SCREEN, WT.END
	};
	
	//modifiers
	public static int[] KEY_MODS = {
		WT.ALT, WT.SHIFT, WT.CTRL, WT.COMMAND 
	};
	
	
	public static int[] extractModifiers(int compositeKey) {
		List keys = new ArrayList();
		addMods(compositeKey, keys);
		addKeys(compositeKey, keys);
		return toIntArray(keys);
	}


	private static void addMods(int compositeKey, List keys) {
		int candidate;
		for (int i = 0; i < KEY_MODS.length; i++) {
			candidate = KEY_MODS[i];
			if ((compositeKey & candidate) == candidate) {
				keys.add(new Integer(candidate));				
			}
		}
	}


	private static void addKeys(int compositeKey, List keys) {
		int candidate;
		for (int i = 0; i < KEY_CONSTANTS.length; i++) {
			candidate = KEY_CONSTANTS[i];
			if ((compositeKey | WT.MODIFIER_MASK) == (candidate | WT.MODIFIER_MASK)) {
				keys.add(new Integer(candidate));				
			}
		}
	}

	
	private static int[] toIntArray(List keys) {
		int size = keys.size();
        int[] intArray = new int[size];
        for (int i=0; i < size; ++i) {
        	intArray[i] = ((Integer)keys.get(i)).intValue();
        }
        return intArray;
	}
}