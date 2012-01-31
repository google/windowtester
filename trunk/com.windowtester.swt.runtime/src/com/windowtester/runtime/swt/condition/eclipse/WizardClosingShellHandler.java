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
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.ShellCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;

/**
 * Dismisses the "Wizard Closing" Shell by clicking the "OK" button.
 */
public class WizardClosingShellHandler extends ShellCondition
	implements IShellConditionHandler
{

	/**
	 * Create a handler that clicks the "OK" button on the "Wizard Closing" Shell.
	 */
	public WizardClosingShellHandler() {
		super("Wizard Closing", true);
	}

	/**
	 * @see IHandler#handle(IUIContext)
	 */
	public void handle(IUIContext ui) {
		try {
			ui.click(new ButtonLocator("OK"));
		}
		catch (WidgetSearchException e) {
			TestCase.fail(e.getLocalizedMessage());
		}
	}

}
