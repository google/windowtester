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
 * Dismisses the "Open Associated Perspective?" Shell by
 * clicking the "yes" or "no" button as specified.
 *
 */
public class ConfirmPerspectiveSwitchShellHandler extends ShellCondition implements IShellConditionHandler {

	private final boolean _switchPerspectives;

	/**
	 * Create a handler that clicks the "no" button on the "Confirm Perspective Switch"
	 * Shell.
	 */
	public ConfirmPerspectiveSwitchShellHandler() {
		this(false);
	}
	
	/**
	 * Create a handler that clicks the "yes" or "no" button on the "Confirm Perspective Switch"
	 * Shell as specified.
	 * @param switchPerspectives if <code>true</code> click "yes", else click "no"
	 */
	public ConfirmPerspectiveSwitchShellHandler(boolean switchPerspectives) {
		super("Open Associated Perspective?", true);
		_switchPerspectives = switchPerspectives;
	}

	/**
	 * @see IHandler#handle(IUIContext)
	 */
	public void handle(IUIContext ui) {
		String buttonText = _switchPerspectives ? "&Yes" : "&No";
		try {
			ui.click(ui.find(new ButtonLocator(buttonText)));
		} catch (WidgetSearchException e) {
			TestCase.fail(e.getLocalizedMessage());
		}
	}
}
