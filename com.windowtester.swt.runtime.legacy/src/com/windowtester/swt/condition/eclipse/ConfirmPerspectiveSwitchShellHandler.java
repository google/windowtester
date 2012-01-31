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
 * A shell handler that dismisses the "Open Associated Perspective?" Shell by
 * clicking the "yes" or "no" button as specified.
 * @deprecated Use {@link com.windowtester.runtime.swt.condition.eclipse.ConfirmPerspectiveSwitchShellHandler} instead.
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
	 * @see com.windowtester.swt.condition.IHandler#handle(com.windowtester.swt.IUIContext)
	 */
	public void handle(IUIContext ui) {
		String buttonText = _switchPerspectives ? "&Yes" : "&No";
		try {
			Widget button = ui.find(new WidgetLocator(Button.class, buttonText));
			ui.click(button);
		} catch (WidgetSearchException e) {
			TestCase.fail(e.getLocalizedMessage());
		}
	}
}
