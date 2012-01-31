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
package com.windowtester.runtime.swt.internal;

import com.windowtester.runtime.swt.internal.text.ITextEntryStrategy;
import com.windowtester.runtime.swt.internal.text.TextEntryStrategy;

/**
 * Helper that manages text entry.
 * 
 */
public class TextDriver {
	
	/*
	 * TODO: this is a bit messy because the text strategy does not 
	 * handle keystrokes, just enterText... when that gets cleaned up, this can too.
	 */
	
	
	private final UIContextSWT ui;

	public TextDriver(UIContextSWT ui) {
		this.ui = ui;
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Text entry actions
	//
	///////////////////////////////////////////////////////////////////////////
		

	public void enterText(String txt) {
		handleConditions();
		getTextEntryStrategy().enterText(ui, txt);
	}


	public void keyClick(int key) {
		handleConditions();
		getTextEntryStrategy().keyClick(ui, key);
	}

	
	public void keyClick(char key) {
		handleConditions();
		getTextEntryStrategy().keyClick(ui, key);
	}


	public void keyClick(int ctrl, char c) {
		handleConditions();
		getTextEntryStrategy().keyClick(ui, ctrl, c);
	}
	

	public void keyDown(char key) {
		handleConditions();
		getTextEntryStrategy().keyDown(ui, key);
	}


	public void keyUp(char key) {
		getTextEntryStrategy().keyUp(ui, key);
		handleConditions(); //handle AFTER!
	}


	public void keyDown(int key) {
		handleConditions();
		getTextEntryStrategy().keyDown(ui, key);
	}
	

	public void keyUp(int key) {
		getTextEntryStrategy().keyUp(ui, key);
		handleConditions(); //handle AFTER!
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// UI Accessors
	//
	///////////////////////////////////////////////////////////////////////////
		

	private ITextEntryStrategy getTextEntryStrategy() {
//		if (isApplicationInNativeContext())
//			return NativeTextEntryStrategy.getInstance();
		return TextEntryStrategy.getCurrent();
	}

//	private boolean isApplicationInNativeContext() {
//		return ui.applicationContext.isNative();
//	}
	
	
	private void handleConditions() {
		//note that this is smart and will not process conditions in the native case
		ui.handleConditions();
	}
}
