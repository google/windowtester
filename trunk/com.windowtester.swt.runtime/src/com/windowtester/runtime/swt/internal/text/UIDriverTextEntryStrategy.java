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
import com.windowtester.runtime.swt.internal.UIContextSWT;
import com.windowtester.runtime.swt.internal.selector.UIDriver;

/**
 * Uses standard UIDriver to enter text.
 */
public class UIDriverTextEntryStrategy implements ITextEntryStrategy {


	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#enterText(com.windowtester.swt.UIContext, java.lang.String)
	 */
	public void enterText(IUIContext ui, String txt) {
		getDriver(ui).enterText(txt);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyClick(com.windowtester.runtime.IUIContext, int)
	 */
	public void keyClick(IUIContext ui, int key) {
		getDriver(ui).keyClick(key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyClick(com.windowtester.runtime.IUIContext, char)
	 */
	public void keyClick(IUIContext ui, char key) {
		getDriver(ui).keyClick(key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyClick(com.windowtester.runtime.IUIContext, int, char)
	 */
	public void keyClick(IUIContext ui, int ctrl, char c) {
		getDriver(ui).keyClick(ctrl, c);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyDown(com.windowtester.runtime.IUIContext, char)
	 */
	public void keyDown(IUIContext ui, char key) {
		getDriver(ui).keyDown(key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyDown(com.windowtester.runtime.IUIContext, int)
	 */
	public void keyDown(IUIContext ui, int key) {
		getDriver(ui).keyDown(key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyUp(com.windowtester.runtime.IUIContext, char)
	 */
	public void keyUp(IUIContext ui, char key) {
		getDriver(ui).keyDown(key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.text.ITextEntryStrategy#keyUp(com.windowtester.runtime.IUIContext, int)
	 */
	public void keyUp(IUIContext ui, int key) {
		getDriver(ui).keyDown(key);
	}
	

	private UIDriver getDriver(IUIContext ui) {
		return ((UIContextSWT)ui).getDriver();
	}
	
}
