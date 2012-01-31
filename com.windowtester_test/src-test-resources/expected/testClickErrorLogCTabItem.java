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
import com.windowtester.runtime.swt.locator.CTabItemLocator; // ?
import com.windowtester.runtime.swt.locator.SWTWidgetLocator; // ? Mac
import org.eclipse.swt.custom.CTabFolder; // ? Mac
import org.eclipse.swt.widgets.Composite; // ? Mac
import com.windowtester.runtime.locator.XYLocator; // ? Mac

public class testClickErrorLogCTabItem extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestClickErrorLogCTabItem() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Error Log"));
		ui.click(new CTabItemLocator("Error Log")); // ?
		ui.click(new XYLocator(new SWTWidgetLocator(CTabFolder.class, 1, // ? Mac
				new SWTWidgetLocator(Composite.class)), 321, 11)); // ? Mac
	}

}