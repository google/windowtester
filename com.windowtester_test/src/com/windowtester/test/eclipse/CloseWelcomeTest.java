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
package com.windowtester.test.eclipse;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.eclipse.WorkbenchLocator;

public class CloseWelcomeTest extends UITestCaseSWT
{
	public void testCloseWelcome() throws Exception {
		IUIContext ui = getUI();
//		try {
//			if (EclipseUtil.isWindows())
//				ui.click(new XYLocator(new CTabItemLocator("Welcome"), 82, 8));
//			else if (Platform.isOSX())
//				ui.click(new XYLocator(new CTabItemLocator("Welcome"), 95, 12));
//			else
//				ui.click(new XYLocator(new CTabItemLocator("Welcome"), 100, 16));
//		}
//		catch (WidgetSearchException e) {
//			// the welcome screen may not be open... just ignore the exception
//		}
//		ui.close(new CTabItemLocator("Welcome"));
//		ui.pause(3000);
		ui.ensureThat(view("Welcome").isClosed());
		ui.ensureThat(new WorkbenchLocator().isMaximized());
	}
}