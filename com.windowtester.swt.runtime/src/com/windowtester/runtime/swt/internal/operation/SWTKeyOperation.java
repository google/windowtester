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
package com.windowtester.runtime.swt.internal.operation;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.KeyStroke;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.runtime.WTLocale;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.text.KeyMapTextEntryStrategy.UnMappedKeyException;
import com.windowtester.runtime.swt.internal.text.KeyStrokeMap;

// This class is derived from three ITextEntryStrategy classes:
// * DelegatingTextEntryStrategy,
// * UIDriverTextEntryStrategy,
// * KeyMapTextEntryStrategy

/**
 * Enter text and trigger commands by pushing key down and key up events onto the OS event
 * queue. Both {@link #keyString(String)} and {@link #queueChar(char)} translate
 * characters based upon the current locale and calls to {@link WT#setLocaleToCurrent()}
 * and {@link WT#resetLocale()}. {@link #keyCode(int)} does not translate characters.
 */
public class SWTKeyOperation extends SWTOperation
{
	private static final VoidCallable SYNC_DELAY = new VoidCallable() {
		public void call() throws Exception {
			// Do nothing... just for synchronization
		}
	};
	private static final char[] ACCENT_CHARS = new char[]{
		0, '^', '¨', '`', '´', '~'
	};

	private static final Map<Integer, Character> VOWEL_CHARS;
	static
	{
	        Map<Integer, Character>tempMap = new HashMap<Integer, Character>();
	        tempMap.put(65, 'a');
	        tempMap.put(69, 'e');
	        tempMap.put(73, 'i');
	        tempMap.put(79, 'o');
	        tempMap.put(85, 'u');
	        VOWEL_CHARS = Collections.unmodifiableMap(tempMap);
	}

   
	
	
	/**
	 * The number of queued OS keystroke events
	 */
	private int eventCount = 0;

	/**
	 * Queue key click events (key down followed by key up) for the characters in the
	 * specified string. The events generated are dependent upon the current locale and
	 * calls to {@link WT#setLocaleToCurrent()} and {@link WT#resetLocale()}.
	 * 
	 * @param txt the string of characters
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTKeyOperation().keyCode(WT.HOME).execute();</code>
	 */
	public SWTKeyOperation keyString(String txt) {
		int txtLen = txt.length();
		for (int i = 0; i < txtLen; i++)
			queueChar(txt.charAt(i));
		return this;
	}

	/**
	 * Queue key click events (key down followed by key up) for the specified keyCode.
	 * Events generated are NOT dependent upon upon the current locale. All uppercase
	 * characters (e.g. 'T') are converted to lower case (e.g. 't') thus
	 * <code>keyCode('T')</code> is equivalent to <code>keyCode('t')</code>. If you want
	 * an upper case character, then call this method with {@link WT#SHIFT} as in
	 * <code>keyCode(WT.SHIFT | 'T')</code> or <code>keyCode(WT.SHIFT | 't')</code>.
	 * 
	 * @param keyCode the code for the key to be queued such as {@link WT#HOME},
	 *            {@link WT#CTRL} | 't', {@link WT#SHIFT} | {@link WT#END}
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTKeyOperation().keyCode(WT.HOME).execute();</code>
	 */
	public SWTKeyOperation keyCode(int keyCode) {
		queueModifierKeysDown(keyCode);
		int unmodified = keyCode - (keyCode & WT.MODIFIER_MASK);

		// Key code characters have the SWT.KEYCODE_BIT bit set
		// whereas unicode characters do not
		if ((unmodified | SWT.KEYCODE_BIT) != 0) {
			queueKeyCodeDown(unmodified);
			queueKeyCodeUp(unmodified);
		}
		else {
			queueCharDown((char) unmodified);
			queueCharUp((char) unmodified);
		}

		queueModifierKeysUp(keyCode);
		return this;
	}

	//=======================================================================
	// Internal setup

	/**
	 * Queue key click events (key down followed by key up) for the specified character.
	 * The events generated are dependent upon the current locale and calls to
	 * {@link WT#setLocaleToCurrent()} and {@link WT#resetLocale()}. If you want to queue
	 * events for characters with accelerators such as {@link WT#CTRL} | 's', call
	 * {@link #keyCode(int)} rather than this method.
	 * 
	 * @param ch the character without accelerators
	 */
	private void queueChar(char ch) {

		// US English keyboard
		
		if (!WTLocale.isCurrent) {
			boolean shift = needsShift(ch);
			if (shift)
				queueKeyCodeDown(WT.SHIFT);
			queueCharDown(ch);
			queueCharUp(ch);
			if (shift)
				queueKeyCodeUp(WT.SHIFT);
			return;
		}

		// Get locale specific keystroke
		// Using the keymaps here
		KeyStroke ks = KeyStrokeMap.getKeyStroke(ch);

		// If not found, then use default mapping to get keystroke 
		if (ks == null)
			ks = KeyStrokeMap.getDefaultKeyStroke(ch);

		// If still not found, then throw an exception
		if (ks == null)
			throw new UnMappedKeyException("Key map not found for " + ch + " for locale "
				+ Locale.getDefault().toString());
		
		// check if key is an accent char
		// these are generated by using two key press
		// the dead key followed by a vowel 
		int accentKey = KeyStrokeMap.getAccentKey(ch);
		if (accentKey > 0) {
			// get the dead key for the accent char
			char accentCh = ACCENT_CHARS[accentKey];
			// key press for dead key
			queueKeystroke(accentCh, KeyStrokeMap.getKeyStroke(accentCh),false);
			// key press for the vowel
			queueKeystroke(ch, ks,true);
		}
		else queueKeystroke(ch, ks,false);
		
	}

	/**
	 * Queue the key events for the specified keystroke
	 * 
	 * @param ch the character
	 * @param ks the keystroke for the character
	 * @param isVowel indicate whether the key press is the vowel for the accent char
	 */
	private void queueKeystroke(char ch, KeyStroke ks,boolean isVowel) {
		int keyCode = ks.getKeyCode();
		int mod = ks.getModifiers();

		boolean ctrl = keyCode == KeyEvent.VK_CONTROL || (mod & InputEvent.CTRL_MASK) != 0;
		boolean alt = keyCode == KeyEvent.VK_ALT || (mod & InputEvent.ALT_MASK) != 0;
		boolean shift = keyCode == KeyEvent.VK_SHIFT || (mod & InputEvent.SHIFT_MASK) != 0;

		StringBuilder sb = new StringBuilder();
		sb.append("keyChar translated '").append(ch);
		sb.append("' into keycode=").append(keyCode);
		if (ctrl)
			sb.append(" ctrl");
		if (alt)
			sb.append(" alt");
		if (shift)
			sb.append(" shift");
		Logger.log(sb.toString());

		if (ctrl)
			queueKeyCodeDown(WT.CTRL);
		if (alt)
			queueKeyCodeDown(WT.ALT);
		if (shift)
			queueKeyCodeDown(WT.SHIFT);
		
		// get the vowel to be pressed using the keycode
		if (isVowel){
			ch = VOWEL_CHARS.get(keyCode);
		}
		
		queueCharDown(ch);
		queueCharUp(ch);
		
		if (shift)
			queueKeyCodeUp(WT.SHIFT);
		if (alt)
			queueKeyCodeUp(WT.ALT);
		if (ctrl)
			queueKeyCodeUp(WT.CTRL);
	}

	/**
	 * Determine if this key requires a shift to dispatch the keyStroke.
	 * 
	 * @param keyCode - the key in question
	 * @return true if a shift event is required.
	 */
	public boolean needsShift(char keyCode) {

		if (keyCode >= 62 && keyCode <= 90)
			return true;
		if (keyCode >= 123 && keyCode <= 126)
			return true;
		if (keyCode >= 33 && keyCode <= 43 && keyCode != 39)
			return true;
		if (keyCode >= 94 && keyCode <= 95)
			return true;
		if (keyCode == 58 || keyCode == 60 || keyCode == 62)
			return true;

		return false;
	}

	/**
	 * Override the superclass implementation to slow down the event generation just
	 * slightly because on the mac, the first character in a long sequence of characters
	 * gets lost and causes that character to appear out of order in the text.
	 */
	protected void queueOSEvent(Event event) {
		super.queueOSEvent(event);
		// 1/100 second pause after each key event - see case 43861
		if (++eventCount < 5)
			queueStep(null);
	}

	//=======================================================================
	// Execution

	/**
	 * Override the superclass implementation to add a brief synchronization delay before
	 * pushing keystroke events on the OS event queue
	 */
	public void execute() {
//		try {
//			displayRef.execute(SYNC_DELAY, 300);
//		}
//		catch (WaitTimedOutException e) {
//			// Ignore timeout because may be blocked on native dialog call
//		}
		super.execute();
	}

	/**
	 * Override the superclass implementation to push keystroke events from the test
	 * thread rather than the UI thread so that we can drive native dialogs.
	 * 
	 * @return <code>true</code> if execution is complete, or <code>false</code> if
	 *         {@link #executeInUI()} should be called again to finish executing after a
	 *         brief delay up to the maximum number of retries.
	 */
	protected boolean executeCallable(int maxWaitTime) {
//		try {
//			return executeInUI();
//		}
//		catch (RuntimeException e) {
//			throw e;
//		}
//		catch (Exception e) {
//			throw new RuntimeException(e);
//		}
		return super.executeCallable(maxWaitTime);
	}
}
