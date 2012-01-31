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
package com.windowtester.swt.condition.eclipse;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Button;

import com.windowtester.swt.IUIContext;
import com.windowtester.swt.WidgetLocator;
import com.windowtester.swt.WidgetSearchException;
import com.windowtester.swt.condition.shell.IShellConditionHandler;
import com.windowtester.swt.condition.shell.ShellCondition;

/**
 * A shell handler that dismisses the "Compiler Settings Changed" Shell by
 * clicking the "yes" or "no" button as specified (yes or no corresponds to whether
 * the project will be rebuilt).
 * @deprecated Use {@link com.windowtester.runtime.swt.condition.eclipse.CompilerSettingsChangedShellHandler} instead.
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
	 * @see com.windowtester.swt.condition.IHandler#handle(com.windowtester.swt.IUIContext)
	 */
	public void handle(IUIContext ui) {
		String buttonText = _doBuild ? "&Yes" : "&No";
		try {
			ui.click(ui.find(new WidgetLocator(Button.class, buttonText)));
		} catch (WidgetSearchException e) {
			TestCase.fail(e.getLocalizedMessage());
		}
	}

}
