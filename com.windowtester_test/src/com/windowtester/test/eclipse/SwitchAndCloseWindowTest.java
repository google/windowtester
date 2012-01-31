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

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.MenuItemLocator;

public class SwitchAndCloseWindowTest extends BaseTest
{
	public void XtestSwitchAndCloseWindowFails() throws Exception {
		IUIContext ui = getUI();
		
		fail("needs to be investigated and fixed");
		
		// Open a new window and then close it
		ui.click(new MenuItemLocator("Window/Open Perspective/Java"));
		ui.click(new MenuItemLocator("Window/New Window"));
		ui.wait(new ShellShowingCondition("Java - Eclipse SDK"));
		
		fail("see https://fogbugz.instantiations.com/default.php?44757");
		//ui.close(new SWTWidgetLocator(Shell.class, "Java - Eclipse SDK"));
		
		ui.wait(new ShellDisposedCondition("Eclipse SDK"));
		
		// Open a new window, switch perspective so that the title changes
		// and then close the window behind it.
		ui.click(new MenuItemLocator("Window/New Window"));
		ui.wait(new ShellShowingCondition("Java - Eclipse SDK"));
		ui.click(new MenuItemLocator("Window/Open Perspective/Debug"));
		fail("see https://fogbugz.instantiations.com/default.php?44757");
		//ui.close(new SWTWidgetLocator(Shell.class, "Java - Eclipse SDK"));
		ui.wait(new ShellDisposedCondition("Eclipse SDK"));
	}

}