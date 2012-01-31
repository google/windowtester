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
package com.windowtester.internal.swing.util;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;



/**
 * Class for decoding keystroke for keyclicks 
 */
public class KeyStrokeDecoder {

	public static int[] KEY_CONSTANTS = { 
		KeyEvent.VK_DOWN, KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
		KeyEvent.VK_ESCAPE, KeyEvent.VK_TAB, KeyEvent.VK_ENTER, 
		KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4,KeyEvent.VK_F5, KeyEvent.VK_F6, 
		KeyEvent.VK_F7, KeyEvent.VK_F8, KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11, KeyEvent.VK_F12,
		KeyEvent.VK_F13, KeyEvent.VK_F14, KeyEvent.VK_F15,
		KeyEvent.VK_HELP, KeyEvent.VK_HOME, KeyEvent.VK_INSERT,
		KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_PAGE_UP, KeyEvent.VK_PRINTSCREEN, KeyEvent.VK_END
	};
	
	//modifiers
	public static int[] KEY_MODS = {
		InputEvent.ALT_DOWN_MASK ,InputEvent.SHIFT_DOWN_MASK , InputEvent.CTRL_DOWN_MASK
	};
	
	public static final int MODIFIER_MASK = InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;

	
	public static int extractModifiers(int compositeKey) {
		int modifiers = 0;
		int candidate;
		for (int i = 0; i < KEY_MODS.length; i++) {
			candidate = KEY_MODS[i];
			if ((compositeKey & candidate) == candidate) {
				modifiers |= candidate;					
			}
		}
		return modifiers;
	}


	
	public static int[] extractKeys(int compositeKey) {
		int candidate;
		List keys = new ArrayList();
		for (int i = 0; i < KEY_CONSTANTS.length; i++) {
			candidate = KEY_CONSTANTS[i];
			if ((compositeKey | MODIFIER_MASK) == (candidate | MODIFIER_MASK)) {
				keys.add(new Integer(candidate));				
			}
		}
		return toIntArray(keys);
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
