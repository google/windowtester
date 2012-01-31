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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;
import abbot.tester.swt.Robot;
import abbot.tester.swt.WidgetLocator;

import com.windowtester.runtime.internal.KeyStrokeDecoder;
import com.windowtester.runtime.swt.internal.operation.SWTKeyOperation;
import com.windowtester.runtime.swt.internal.operation.SWTPushEventOperation;
import com.windowtester.runtime.swt.internal.state.MouseConfig;

/**
 * Posts primitive events directly to the SWT event queue.
 */
public class DisplayEventDispatcher {

	protected Point pointT = new Point(0, 0);

	// keyclick on GTK helper
	abbot.swt.Robot _robot;

	////////////////////////////////////////////////////////////////////////////
	//
	// Primitive event posting actions
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Dispatch a keyClick(keyUp..keyDown) event.
	 * 
	 * @param keyCode
	 *            - the key to click.
	 */
	public void keyClick(int keyCode) {
//		keyDown(keyCode);
//		keyUp(keyCode);
		new SWTKeyOperation().keyCode(keyCode).execute();
	}

	/**
	 * Dispatch a keyClick(keyUp..keyDown) event.
	 * 
	 * @param keyCode
	 *            - the key to click.
	 */
	public void keyClick(char keyCode) {

		/*
		 * Work-around for gtk where we rely on the Robot to handle mappings for
		 * us
		 */
		if (SWT.getPlatform().equals("gtk") || Platform.isOSX()) { // Mac
			// testing
			// (fix $
			// problem)
			robotKeyClick(keyCode);
			return;
		}

		/*
		 * In the non-gtk case, stick with our original strategy
		 */

		boolean shift = needsShift(keyCode);

		if (shift)
			keyDown(SWT.SHIFT);

		keyDown(keyCode); // for some reason $ becomes the +/- char on Mac, so
		// see above
		keyUp(keyCode);

		if (shift)
			keyUp(SWT.SHIFT);
	}

	/**
	 * Key click method that delegates to SWT Robot
	 * 
	 * @param keyCode
	 *            - the key to click.
	 */
	public void robotKeyClick(final char keyCode) {
		// robot takes care of figuring what modifies are used
		final abbot.swt.Robot robot = getAbbotRobot();
		// wrap in a sync exec to try to patch repeated entries on GTK
		// due to OS load (conjecture from John: SWT thinks the user wants that
		// character to be repeated a bunch of times because their finger
		// hasn't left the keyboard while the even queue is processing other
		// stuff.
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				robot.keyPress((int) keyCode);
				robot.keyRelease((int) keyCode);
			}
		});
	}

	/**
	 * Determine if this key requires a shift to dispatch the keyStroke.
	 * 
	 * @param keyCode
	 *            - the key in question
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
	 * Dispatch a keyUp event.
	 * 
	 * @param keyCode
	 *            - the key to release.
	 */
	public void keyUp(int keyCode) {
		int[] keys = extractKeys(keyCode);
		// NOTICE: this is done in reverse order!
		for (int i = keys.length - 1; i >= 0; --i)
			doKeyUp(keys[i]);
	}

	private void doKeyUp(final int keyCode) {
		SpecialKeyHandler.preUp(keyCode);
		// trace("post key up " + keyCode);
		Event event = new Event();
		event.type = SWT.KeyUp;
		event.keyCode = keyCode;
		new SWTPushEventOperation(event).execute();
	}



	/**
	 * Dispatch a keyDown event.
	 * 
	 * @param keyCode
	 *            - the key to press.
	 */
	public void keyDown(int keyCode) {
		int[] keys = extractKeys(keyCode);
		for (int i = 0; i < keys.length; ++i)
			doKeyDown(keys[i]);
	}

	/**
	 * Extract discrete keys from this (possibly) compound key.
	 */
	private int[] extractKeys(int keyCode) {
		return KeyStrokeDecoder.extractModifiers(keyCode);
	}

	private void doKeyDown(final int keyCode) {
		SpecialKeyHandler.preDown(keyCode);
		// trace("post key down " + keyCode);
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.keyCode = keyCode;
		new SWTPushEventOperation(event).execute();
	}

	/**
	 * Dispatch a keyUp event.
	 * 
	 * @param keyCode
	 *            - the key to release.
	 */
	public void keyUp(final char keyCode) {
		// trace("post key up " + keyCode);
		Event event = new Event();
		event.type = SWT.KeyUp;
		event.character = keyCode;
		new SWTPushEventOperation(event).execute();
	}

	/**
	 * Dispatch a keyDown event.
	 * 
	 * @param keyCode
	 *            - the key to press.
	 */
	public void keyDown(final char keyCode) {
		// trace("post key down " + keyCode);
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.character = keyCode;
		new SWTPushEventOperation(event).execute();
	}

	/**
	 * Dispatch a stream of keyClick events (one per character).
	 * 
	 * @param str
	 *            - the String of characters to keyClick.
	 */
	public void enterText(String str) {
		if (str == null)
			return;
		for (int i = 0; i < str.length(); i++) {
			keyClick(str.charAt(i));
		}
	}

	/**
	 * Dispatch a mousePress event.
	 * 
	 * @param accelerator
	 *            - the mouse accelerator.
	 */
	public void mouseDown(int accelerator) {

		Event event = createClickEvent(accelerator);
		event.type = SWT.MouseDown;
		// !pq:
		// event.stateMask = stateMask;

		new SWTPushEventOperation(event).execute();
	}

	/**
	 * Dispatch a mouseRelease event.
	 * 
	 * @param accelerator
	 *            - the mouse accelerator.
	 */
	public void mouseUp(int accelerator) {

		Event event = createClickEvent(accelerator);
		event.type = SWT.MouseUp;
		new SWTPushEventOperation(event).execute();
	}

	/**
	 * Dispatch a MouseDoubleClick event.
	 * 
	 * @param accelerator
	 *            - the mouse accelerator.
	 */
	public void mouseDoubleClick(int accelerator) {

		Event event = createClickEvent(accelerator);
		event.type = SWT.MouseDoubleClick;
		// !pq:
		// event.stateMask = stateMask;

		new SWTPushEventOperation(event).execute();
	}

	/**
	 * Dispatch a mouseMove event that moves the mouse to this x,y offset from
	 * the top left corner of the given widget.
	 * 
	 * @param w
	 *            - the widget to whcih to move the mouse
	 * @param x
	 *            - the x offset
	 * @param y
	 *            - the y offset
	 */
	public synchronized void mouseMove(final Widget w, int x, int y) {
		pointT = null;
		Robot.syncExec(w.getDisplay(), this, new Runnable() {
			public void run() {
				pointT = WidgetLocator.getLocation(w);
			}
		});
		if (pointT == null) // TODO added for Mac testing
			return;
		mouseMove(pointT.x + x, pointT.y + y);
	}

	/**
	 * Dispatch a mouseMove event that moves the mouse to this x,y coordinate.
	 * 
	 * @param x
	 *            - the x coordinate
	 * @param y
	 *            - the y coordinate
	 */
	public void mouseMove(int x, int y) {
		Event event = new Event();
		event.type = SWT.MouseMove;
		event.x = x;
		event.y = y;
		new SWTPushEventOperation(event).execute();
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Internal
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Create a mouse event for the button associated with this accelerator.
	 */
	private static final Event createClickEvent(int accelerator) {
		Event event = new Event();
		event.button = MouseConfig.getButton(accelerator);
		return event;
	}

	// get an abbot robot helper
	private abbot.swt.Robot getAbbotRobot() {
		if (_robot == null)
			_robot = new abbot.swt.Robot();
		return _robot;
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// Timing
	//
	// /////////////////////////////////////////////////////////////////////////

	public static void pause(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
		}
	}

}
