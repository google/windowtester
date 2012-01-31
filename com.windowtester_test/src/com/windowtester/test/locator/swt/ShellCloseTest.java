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
package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.ShellLocator;
import com.windowtester.test.locator.swt.shells.ButtonTestShell;

public class ShellCloseTest extends AbstractLocatorTest {

	ButtonTestShell window;
	
	@Override
	public void uiSetup() {
		window = new ButtonTestShell();
		window.open();
		wait(new ButtonLocator("button").isVisible());
	}
	
	@Override
	public void uiTearDown() {
		Shell shell = window.getShell();
		try {
		if (!shell.isDisposed())
			shell.dispose();
		} catch (Exception e) {
			//ignore teardown exception
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public void testLegacyCloseShellLocator() throws Exception {
		IUIContext ui = getUI();
		ui.close(new ShellLocator(shellText()));	
		ui.assertThat(new ShellLocator(shellText()).isClosed());
	}

	@SuppressWarnings("deprecation")
	public void testLegacyCloseWidgetLocator() throws Exception {
		IUIContext ui = getUI();
		ui.close(new SWTWidgetLocator(Shell.class, shellText()));	
		ui.assertThat(new ShellLocator(shellText()).isClosed());
	}	
	
	public void testCloseShellLocator() throws Exception {
		IUIContext ui = getUI();
		ui.ensureThat(new ShellLocator(shellText()).isClosed());	
		ui.assertThat(new ShellLocator(shellText()).isClosed());
	}
	
	private String shellText() {
		return ButtonTestShell.SHELL_LABEL;
	}
	
}
