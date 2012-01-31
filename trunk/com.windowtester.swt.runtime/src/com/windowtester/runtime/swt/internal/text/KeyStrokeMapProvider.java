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
package com.windowtester.runtime.swt.internal.text;

import java.util.Map;

/** Provides read/write of local-specific mappings for virtual keycode-based
    KeyStrokes to characters and vice versa.  
*/
public interface KeyStrokeMapProvider {
    /** Returns a map for the current locale which translates an Integer
     * virtual keycode (VK_XXX) into a the Character it produces.  May not
     * necessarily map all keycode/modifier combinations.
     */
    Map loadCharacterMap(); 

    /** Returns a map for the current locale which translates a Character into
     * a keycode-based KeyStroke.  Where multiple keycodes may produce the
     * same Character output, the simplest keystroke is used.
     */
    Map loadKeyStrokeMap();
    
    /** Returns a map for the default locale which translates a Character into
     * a keycode-based KeyStroke. 
     * 
     */
    Map loadDefaultKeyStrokeMap();
    
    /** Returns a map for the accent char keys in the current locale
     * 
     */
    Map loadAccentKeyMap();
}
