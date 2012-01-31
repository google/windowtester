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
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

public class testRunToolItemPullDown extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestRunToolItemPullDown() throws Exception {
		IUIContext ui = getUI();
		ui
				.click(new PullDownMenuItemLocator(
						"Run Configurations...",				   // ? 3.4
						"Open Run Dialog...",                      // ? 3.3
						"Run...",                                  // ? 3.2
						new ContributedToolItemLocator(
								"org.eclipse.debug.internal.ui.actions.RunDropDownAction")));
		ui.wait(new ShellShowingCondition("Run"));                 // ?
		ui.wait(new ShellShowingCondition("Run Configurations"));  // ? 3.4M7+
		ui.click(new ButtonLocator("Close"));
		ui.wait(new ShellDisposedCondition("Run"));                // ? 
		ui.wait(new ShellDisposedCondition("Run Configurations")); // ? 3.4M7+
	}
	
}
