package com.windowtester.test.locator.swt;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.TreeCellLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.helpers.JavaProjectHelper;
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
public class TreeCellLocatorPDETest extends BaseTest {
	
	public void testClickItemInNavigator() throws Exception {
		IUIContext ui = getUI();
		String projectName = getClass().getSimpleName() + "Project";
		JavaProjectHelper.createJavaProject(ui, projectName);
		WorkBenchHelper.openView(ui, View.JAVA_PACKAGEEXPLORER);
		ui.click(new TreeCellLocator(projectName).in(new ViewLocator(View.JAVA_PACKAGEEXPLORER.getViewID())));
	}
}
