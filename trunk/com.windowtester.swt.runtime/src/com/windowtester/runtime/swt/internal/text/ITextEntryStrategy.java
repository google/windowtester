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
 * A strategy for entering text.
 */
public interface ITextEntryStrategy {
	
	
	/*
	 * Known customer dependents (impacted by refactoring):
	 * 
	 * 
	 */
	
	/** Enter the given text in the given UIContext.
	 */
	void enterText(IUIContext ui, String txt);
	
	void keyClick(IUIContext ui, int key);
	
	void keyClick(IUIContext ui, char key);

	public void keyClick(IUIContext ui, int ctrl, char c);
	
	public void keyDown(IUIContext ui, char key);

	public void keyUp(IUIContext ui, char key);

	public void keyDown(IUIContext ui, int key);
	
	public void keyUp(IUIContext ui, int key);
}
