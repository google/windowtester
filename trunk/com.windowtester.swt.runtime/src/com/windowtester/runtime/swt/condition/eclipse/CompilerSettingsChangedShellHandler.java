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
package com.windowtester.runtime.swt.condition.eclipse;

import junit.framework.TestCase;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IHandler;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.ShellCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;

/**
 * Dismisses the "Compiler Settings Changed" Shell by
 * clicking the "yes" or "no" button as specified (yes or no corresponds to whether
 * the project will be rebuilt).
 *
 */
public class CompilerSettingsChangedShellHandler extends ShellCondition implements IShellConditionHandler {
	
	private final boolean _doBuild;

	/**
	 * Create a handler that clicks the "yes" or "no" button on the  "Compiler Settings Changed"
	 * Shell as specified.
	 * @param doBuild if <code>true</code> click "yes", else click "no"
	 */
	public CompilerSettingsChangedShellHandler(boolean doBuild) {
		super("Compiler Settings Changed", true);
		_doBuild          = doBuild;
	}
	
	/**
	 * @see IHandler#handle(IUIContext)
	 */
	public void handle(IUIContext ui) {
		String buttonText = _doBuild ? "&Yes" : "&No";
		try {
			ui.click(ui.find(new ButtonLocator(buttonText)));
		} catch (WidgetSearchException e) {
			TestCase.fail(e.getLocalizedMessage());
		}
	}

}
