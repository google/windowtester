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

import com.windowtester.internal.runtime.WTLocale;
import com.windowtester.runtime.IUIContext;

/**
 * A text entry strategy that optionally uses a keymap strategy if
 * the locale is non English.
 */

public class DelegatingTextEntryStrategy implements ITextEntryStrategy {

	private final ITextEntryStrategy defaultStrategy = new UIDriverTextEntryStrategy();

    private final ITextEntryStrategy keyMapStrategy = new KeyMapTextEntryStrategy();
    
	public ITextEntryStrategy getKeyMapStrategy() {
		return keyMapStrategy;
	}
	
	public ITextEntryStrategy getDefaultStrategy() {
		return defaultStrategy;
	}

	/**
	 * Returns the currently active text strategy.
	 */
	public ITextEntryStrategy getActiveStrategy() {
		return WTLocale.isCurrent ? getKeyMapStrategy() : getDefaultStrategy();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#enterText(com.windowtester.swt.UIContext, java.lang.String)
	 */
	public void enterText(IUIContext ui, String txt) {
		getActiveStrategy().enterText(ui, txt);
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyClick(char)
	 */
	public void keyClick(IUIContext ui, char key) {
		getActiveStrategy().keyClick(ui, key);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyClick(int, char)
	 */
	public void keyClick(IUIContext ui, int ctrl, char c) {
		getActiveStrategy().keyClick(ui, ctrl, c);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyClick(int)
	 */
	public void keyClick(IUIContext ui, int key) {
		getActiveStrategy().keyClick(ui, key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyDown(char)
	 */
	public void keyDown(IUIContext ui, char key) {
		getActiveStrategy().keyDown(ui, key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyDown(int)
	 */
	public void keyDown(IUIContext ui, int key) {
		getActiveStrategy().keyDown(ui, key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyUp(char)
	 */
	public void keyUp(IUIContext ui, char key) {
		getActiveStrategy().keyUp(ui, key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyUp(int)
	 */
	public void keyUp(IUIContext ui, int key) {
		getActiveStrategy().keyUp(ui, key);		
	}
	
	
	
	 /** test locale  */
 /*   public static void main(String[] args) {
    	Locale locale = Locale.getDefault();
    	locale = new Locale("en","CA");
    	System.out.println(locale.getDisplayLanguage().toString());
    	boolean result = locale.getDisplayLanguage().toString().equals("English");
    	System.out.println(result);
        System.exit(1);
    }
*/
}
