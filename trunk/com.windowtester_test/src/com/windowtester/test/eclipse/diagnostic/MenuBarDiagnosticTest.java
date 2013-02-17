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
package com.windowtester.test.eclipse.diagnostic;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;

public class MenuBarDiagnosticTest extends UITestCaseSWT {

//	Menu {&File, &Edit, &Navigate, Se&arch, &Project, &CodePro, &Run, &Window, &Help}<HC|13624414>
	 
	//CodePro menu may not be present...
	String[] labels = {"File", "Edit", "Navigate", "Search","Project", "CodePro", "Run", "Window", "Help"};
	
	public void testDrive() throws Exception {
		IUIContext ui = getUI();
		for (String label : labels) {
			ui.click(new MenuItemLocator(label));
			ui.wait(TimeElapsedCondition.milliseconds(1000));
			ui.keyClick(WT.ESC); //to dismiss
		}
		
		
	}
	
//	@Override
//	protected void tearDown() throws Exception {
//		//helpful to see what menus are actually visible
//		new DebugHelper().printWidgets();
//	}
	
}
