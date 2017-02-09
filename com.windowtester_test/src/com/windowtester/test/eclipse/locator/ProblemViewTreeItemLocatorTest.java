package com.windowtester.test.eclipse.locator;

import static com.windowtester.test.eclipse.helpers.JavaProjectHelper.createJavaClass;
import static com.windowtester.test.eclipse.helpers.JavaProjectHelper.createJavaProject;
import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.openView;
import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.View.BASIC_PROBLEMS;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.condition.eclipse.ActiveEditorCondition;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.EclipseUtil;


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
public class ProblemViewTreeItemLocatorTest extends BaseTest {

	
	public void testClickItemInProblemView() throws Exception {
		IUIContext ui = getUI();
		try{
			createJavaProject(ui, getProjectName());
			createJavaClass(ui, getSrcFolder(), "MyClass");
			ui.wait(ActiveEditorCondition.forName("MyClass.java"));
			ui.enterText("bang");
			ui.click(new MenuItemLocator("File/Save"));
			openView(ui, BASIC_PROBLEMS);
			verifyProblemReported();
		}finally{
			ui.ensureThat(new CTabItemLocator("MyClass.java").isClosed());
		}
	}

	private void verifyProblemReported() throws WidgetSearchException {
		TreeItemLocator itemLocator = new TreeItemLocator("Errors (.*)/Syntax error on token \"bang\", delete this token", new ViewLocator("org.eclipse.ui.views.ProblemView"));
		//the rub here is that in Eclipse 34+ the dynamic tree is not populated (expanded) so the path is not visible to an assertion
		//in that case, we do a verification by selection
		if (eclipseVersionIsLessThan34())
			verifyVisibility(itemLocator);
		else
			verifyBySelection(itemLocator);
			
	}

	private boolean eclipseVersionIsLessThan34() {
		return EclipseUtil.getMajor() <= 3 && EclipseUtil.getMinor() < 4;
	}

	private void verifyVisibility(TreeItemLocator itemLocator)
			throws WaitTimedOutException {
		getUI().assertThat(itemLocator.isVisible());
	}

	private IWidgetLocator verifyBySelection(TreeItemLocator itemLocator)
			throws WidgetSearchException {
		return getUI().click(itemLocator);
	}

	private String getSrcFolder() {
		return getProjectName() + "/src";
	}

	private String getProjectName() {
		return getClass().getName();
	}
	
}
