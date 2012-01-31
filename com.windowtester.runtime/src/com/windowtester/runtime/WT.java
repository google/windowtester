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
package com.windowtester.runtime;

import java.util.Locale;

import com.windowtester.internal.runtime.WTLocale;
import com.windowtester.runtime.condition.ICondition;


/**
 * WindowTester Constants 
 */
public class WT
{
	
	
	// Button mask constants used in IUIContext
	// Note: these constants map directly to constants of the same name 
	// in the SWT constant class
	public static final int NONE = 0;
	public static final int ALT = 1 << 16;
	public static final int SHIFT = 1 << 17;
	public static final int CTRL = 1 << 18;
	public static final int COMMAND = 1 << 22;
	
	public static final char TAB = '\t';
	public static final char CR = '\r';
	public static final char ESC = 0x1B;
	public static final char BS = '\b';
	public static final char DEL = 0x7F;

	public static final int MODIFIER_MASK =  ALT | SHIFT | CTRL | COMMAND;
	
	public static final int KEYCODE_BIT = (1 << 24);
	public static final int ARROW_UP = KEYCODE_BIT + 1;
	public static final int ARROW_DOWN = KEYCODE_BIT + 2;
	public static final int ARROW_LEFT = KEYCODE_BIT + 3;
	public static final int ARROW_RIGHT = KEYCODE_BIT + 4;
	
	public static final int PAGE_UP = KEYCODE_BIT + 5;
	public static final int PAGE_DOWN = KEYCODE_BIT + 6;
	public static final int HOME = KEYCODE_BIT + 7;
	public static final int END = KEYCODE_BIT + 8;
	public static final int INSERT = KEYCODE_BIT + 9;

	public static final int F1 = KEYCODE_BIT + 10;
	public static final int F2 = KEYCODE_BIT + 11;
	public static final int F3 = KEYCODE_BIT + 12;
	public static final int F4 = KEYCODE_BIT + 13;
	public static final int F5 = KEYCODE_BIT + 14;
	public static final int F6 = KEYCODE_BIT + 15;
	public static final int F7 = KEYCODE_BIT + 16;
	public static final int F8 = KEYCODE_BIT + 17;
	public static final int F9 = KEYCODE_BIT + 18;
	public static final int F10 = KEYCODE_BIT + 19;
	public static final int F11 = KEYCODE_BIT + 20;
	public static final int F12 = KEYCODE_BIT + 21;
	public static final int F13 = KEYCODE_BIT + 22;
	public static final int F14 = KEYCODE_BIT + 23;
	public static final int F15 = KEYCODE_BIT + 24;

	public static final int KEYPAD_MULTIPLY = KEYCODE_BIT + 42;
	public static final int KEYPAD_ADD = KEYCODE_BIT + 43;
	public static final int KEYPAD_SUBTRACT = KEYCODE_BIT + 45;
	public static final int KEYPAD_DECIMAL = KEYCODE_BIT + 46;
	public static final int KEYPAD_DIVIDE = KEYCODE_BIT + 47;
	public static final int KEYPAD_0 = KEYCODE_BIT + 48;
	public static final int KEYPAD_1 = KEYCODE_BIT + 49;
	public static final int KEYPAD_2 = KEYCODE_BIT + 50;
	public static final int KEYPAD_3 = KEYCODE_BIT + 51;
	public static final int KEYPAD_4 = KEYCODE_BIT + 52;
	public static final int KEYPAD_5 = KEYCODE_BIT + 53;
	public static final int KEYPAD_6 = KEYCODE_BIT + 54;
	public static final int KEYPAD_7 = KEYCODE_BIT + 55;
	public static final int KEYPAD_8 = KEYCODE_BIT + 56;
	public static final int KEYPAD_9 = KEYCODE_BIT + 57;
	public static final int KEYPAD_EQUAL = KEYCODE_BIT + 61;
	public static final int KEYPAD_CR = KEYCODE_BIT + 80;

	public static final int HELP = KEYCODE_BIT + 81;
	public static final int CAPS_LOCK = KEYCODE_BIT + 82;
	public static final int NUM_LOCK = KEYCODE_BIT + 83;
	public static final int SCROLL_LOCK = KEYCODE_BIT + 84;
	public static final int PAUSE = KEYCODE_BIT + 85;
	public static final int BREAK = KEYCODE_BIT + 86;
	public static final int PRINT_SCREEN = KEYCODE_BIT + 87;
	
	
	
	
	public static final int BUTTON1 = 1 << 19;
	public static final int BUTTON2 = 1 << 20;
	public static final int BUTTON3 = 1 << 21;
	public static final int BUTTON4 = 1 << 23;
	public static final int BUTTON5 = 1 << 25;
	public static final int BUTTON_MASK = BUTTON1 | BUTTON2 | BUTTON3 | BUTTON4 | BUTTON5;
	
	// Selection constants used in Locators
	public static final int CHECK = 1 << 5; //note this matches SWT.CHECK 
	
	// Variation constants used in Locators
	public static final int NO_SMART_MATCH = 1 << 0;
	public static final int CLOSE = 1 << 1;

	// Defaults used in IUIContext wait methods... in milliseconds
	private static long DEFAULT_WAIT_TIMEOUT = 60000;
	private static int DEFAULT_WAIT_INTERVAL = 10;
	
	/**
	 * Get the number of milliseconds to wait for a condition to be true
	 * before timing out.
	 * @see #setDefaultWaitTimeOut(long)
	 * @return the wait timeout in milliseconds
	 */
	public static long getDefaultWaitTimeOut() {
		return DEFAULT_WAIT_TIMEOUT;
	}
	
	/**
	 * Set the number of milliseconds to wait for a condition to be true
	 * before timing out.
	 * <p>
	 * This value will be used in all cases where the wait timeout is <em>unspecified</em>,
	 * such as:
	 * <pre>
	 * getUI().wait(new ICondition(){...}); //default timeout and interval used
	 * </pre>
	 * The wait timeout can always be specified, in which case this default is overridden:
	 * <pre>
	 * getUI().wait(new ICondition(){...}, 500, 5); //timeout at 500 ms, testing at 5 ms intervals 
	 * </pre> 
	 * See {@link IUIContext#wait(ICondition)}.
	 * @param ms the wait timeout in milliseconds
	 */
	public static void setDefaultWaitTimeOut(long ms)  {
		DEFAULT_WAIT_TIMEOUT = ms;
	}
	
	/**
	 * Get the number of milliseconds to wait in between tests of a condition.
	 * 
	 * @see #setDefaultWaitInterval(int).
	 * @return the number of milliseconds to wait in between condition tests
	 */
	public static int getDefaultWaitInterval() {
		return DEFAULT_WAIT_INTERVAL;
	}
	
	/**
	 * Set the number of milliseconds to wait in between tests of a condition. 
	 * <p>
	 * This value will be used in all cases where the wait interval is <em>unspecified</em>,
	 * such as:
	 * <pre>
	 * getUI().wait(new ICondition(){...}); //default timeout and interval used
	 * </pre>
	 * The wait interval can always be specified, in which case this default is overridden:
	 * <pre>
	 * getUI().wait(new ICondition(){...}, 500, 5); //timeout at 500 ms, testing at 5 ms intervals 
	 * </pre> 
	 * See {@link IUIContext#wait(ICondition)}.
	 * @param ms the number of milliseconds to wait in between condition tests
	 */
	public static void setDefaultWaitInterval(int ms)  {
		DEFAULT_WAIT_INTERVAL = ms;
	}
	
	/**
	 * Gets the current locale and sets the keyboard type correspondingly. 
	 * 
	 *  For eg. If current locale is German / Germany, and the test needs to 
	 *  use the German Keyboard to enter text, then in the test have the statement
	 *  
	 *  WT.setLocaleToCurrent();
	 *  
	 *  From then on, all text will be entered using the German keyboard. At the end
	 *  of the tests, in order to set it back to the US Keyboard use 
	 *  
	 *  WT.resetWTLocale();
	 * 
	 */
	public static void setLocaleToCurrent(){
		// added check for German/US Key-mapping Issues
		if (!Locale.getDefault().equals(Locale.US)) {
			WTLocale.isCurrent = true;
		}
		
	}

	/**
	 * Sets the keyboard type to a US keyboard.
	 */
	public static void resetLocale(){
		WTLocale.isCurrent = false;
	}
	
	
}
