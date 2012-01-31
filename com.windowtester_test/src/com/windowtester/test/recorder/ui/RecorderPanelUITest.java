package com.windowtester.test.recorder.ui;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;

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
public class RecorderPanelUITest extends UITestCaseSWT {

	
	private static final String WT_VIEWS_CATEGORY = "Window Tester";
	private static final String WT_RECORDER_CONSOLE_VIEW = "Recorder Console";
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		showView();
	}

	
	
	public void testBasic() {
		
		
	}
	
	
	
	private void showView() throws WidgetSearchException {
		closeWelcomePageIfNecessary();
		getUI().click(new MenuItemLocator("Window/Show View/" + WT_VIEWS_CATEGORY + "/" + WT_RECORDER_CONSOLE_VIEW));
		//TODO: wait for view to be up!
	}
	
	
	
	protected void closeWelcomePageIfNecessary() throws WidgetSearchException {
		IWidgetLocator[] welcomeTab = getUI().findAll(new CTabItemLocator("Welcome"));
		if (welcomeTab.length == 0)
			return;
		getUI().click(new XYLocator(welcomeTab[0], 78, 12));
	}

	
	
	
}
