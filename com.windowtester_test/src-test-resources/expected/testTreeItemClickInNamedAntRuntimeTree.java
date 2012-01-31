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
package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

public class testTreeItemClickInNamedAntRuntimeTree extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestTreeItemClickInNamedAntRuntimeTree() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Preferences..."));
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new TreeItemLocator("Ant")); // ? generated if not already expanded
		ui.enterText("+");                    // ? generated if not already expanded
		ui.click(new TreeItemLocator("Ant/Runtime"));
		ui.click(new TreeItemLocator("Contributed Entries",
				new NamedWidgetLocator("named.tree")));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}

}