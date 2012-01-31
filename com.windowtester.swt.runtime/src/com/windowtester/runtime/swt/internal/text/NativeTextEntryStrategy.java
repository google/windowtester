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

import com.windowtester.runtime.IUIContext;


/**
 * @deprecated
 */
public class NativeTextEntryStrategy implements ITextEntryStrategy {

	private static final NativeTextEntryStrategy instance = new NativeTextEntryStrategy();
	
	private final abbot.tester.Robot robot = new abbot.tester.Robot();
	
	public static ITextEntryStrategy getInstance() {
		return instance;
	}
	
	public void enterText(IUIContext ui, String txt) {
		robot.keyString(txt);
	}

	public void keyClick(IUIContext ui, int key) {
		robot.key(key);
	}

	public void keyClick(IUIContext ui, char key) {
		robot.keyStroke(key);
	}

	public void keyClick(IUIContext ui, int ctrl, char c) {
		robot.key(c, ctrl);
	}

	public void keyDown(IUIContext ui, char key) {
		robot.keyPress(key);
	}

	public void keyDown(IUIContext ui, int key) {
		robot.keyPress(key);
	}

	public void keyUp(IUIContext ui, char key) {
		robot.keyRelease(key);
	}

	public void keyUp(IUIContext ui, int key) {
		robot.keyRelease(key);
	}

}
