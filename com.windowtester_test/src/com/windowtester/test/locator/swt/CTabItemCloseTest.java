package com.windowtester.test.locator.swt;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.eclipse.ViewShowingCondition;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper.View;

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
public class CTabItemCloseTest extends BaseTest  {

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUIContext ui = getUI();
		WorkBenchHelper.openView(ui, View.JAVA_PACKAGEEXPLORER);
		WorkBenchHelper.openView(ui, View.BASIC_NAVIGATOR);
	}
	
	public void testCloseViewByTabItemLocator() throws Exception {
		IUIContext ui = getUI();
		ui.ensureThat(new CTabItemLocator("Package Explorer").isClosed());
		ui.wait(new ViewShowingCondition(View.JAVA_PACKAGEEXPLORER.getViewID()).not());
		
		ui.ensureThat(new CTabItemLocator("Navigator").isClosed());
		ui.wait(new ViewShowingCondition(View.BASIC_NAVIGATOR.getViewID()).not());
		
	}
	
	@SuppressWarnings("deprecation")
	public void testLegacyCloseViewByTabItemLocator() throws Exception {
		IUIContext ui = getUI();
		ui.close(new CTabItemLocator("Package Explorer"));
		ui.wait(new ViewShowingCondition(View.JAVA_PACKAGEEXPLORER.getViewID()).not());
		
		ui.close(new CTabItemLocator("Navigator"));
		ui.wait(new ViewShowingCondition(View.BASIC_NAVIGATOR.getViewID()).not());
	}
	
	
	//view locator tests moved to ViewLocatorSmokeTest
	
}
