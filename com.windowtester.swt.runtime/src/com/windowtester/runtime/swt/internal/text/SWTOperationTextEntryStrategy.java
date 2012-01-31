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
import com.windowtester.runtime.swt.internal.operation.SWTKeyOperation;

/**
 * New text entry strategy equivalent to {@link DelegatingTextEntryStrategy},
 * {@link UIDriverTextEntryStrategy} and {@link KeyMapTextEntryStrategy} but utilizing
 * calls to {@link SWTKeyOperation}.
 */
public class SWTOperationTextEntryStrategy
	implements ITextEntryStrategy
{
	public void enterText(IUIContext ui, String txt) {
		new SWTKeyOperation().keyString(txt).execute();
	}

	public void keyClick(IUIContext ui, int key) {
		throw new RuntimeException("Not implemented: Modify caller to invoke a different method.");
	}

	public void keyClick(IUIContext ui, char key) {
		throw new RuntimeException("Not implemented: Modify caller to invoke a different method.");
	}

	public void keyClick(IUIContext ui, int ctrl, char c) {
		throw new RuntimeException("Not implemented: Modify caller to invoke a different method.");
	}

	public void keyDown(IUIContext ui, char key) {
		throw new RuntimeException("Not implemented: Modify caller to invoke a different method.");
	}

	public void keyDown(IUIContext ui, int key) {
		throw new RuntimeException("Not implemented: Modify caller to invoke a different method.");
	}

	public void keyUp(IUIContext ui, char key) {
		throw new RuntimeException("Not implemented: Modify caller to invoke a different method.");
	}

	public void keyUp(IUIContext ui, int key) {
		throw new RuntimeException("Not implemented: Modify caller to invoke a different method.");
	}
}
