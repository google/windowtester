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
import org.eclipse.swt.widgets.Widget;

import com.windowtester.swt.IUIContext;
import com.windowtester.swt.WidgetLocator;
import com.windowtester.swt.WidgetSearchException;
import com.windowtester.swt.condition.shell.IShellConditionHandler;
import com.windowtester.swt.condition.shell.ShellCondition;

/**
 * A shell handler that dismisses the "Wizard Closing" Shell by
 * clicking the "OK" button.
 * @deprecated Use {@link com.windowtester.runtime.swt.condition.eclipse.WizardClosingShellHandler} instead.
 */
public class WizardClosingShellHandler extends ShellCondition implements IShellConditionHandler {

	/**
	 * Create a handler that clicks the "OK" button on the "Wizard Closing"
	 * Shell.
	 */
	public WizardClosingShellHandler() {
		super("Wizard Closing", true);
	}
	
	/**
	 * @see com.windowtester.swt.condition.IHandler#handle(com.windowtester.swt.IUIContext)
	 */
	public void handle(IUIContext ui) {
		try {
			Widget button = ui.find(new WidgetLocator(Button.class, "OK"));
			ui.click(button);
		} catch (WidgetSearchException e) {
			TestCase.fail(e.getLocalizedMessage());
		}
	}

}
