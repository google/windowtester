package com.windowtester.test.eclipse;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.eclipse.WorkbenchLocator;

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
public class TestTreeItemViewScope extends UITestCaseSWT {

	/* @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		IUIContext ui = getUI();
		ui.ensureThat(new WorkbenchLocator().hasFocus());
		ui.ensureThat(ViewLocator.forName("Welcome").isClosed());
	}

	/**
	 * Main test method.
	 */
	public void testTestTree() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new FilteredTreeItemLocator(
				"Plug-in Development/Plug-in Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("test");
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellShowingCondition("Open Associated Perspective?"));
		ui.click(new ButtonLocator("&Yes"));
		ui.wait(new ShellDisposedCondition("New Plug-in Project"));
		ui.click(2, new TreeItemLocator("test/src/test/Activator.java",
				new ViewLocator("org.eclipse.jdt.ui.PackageExplorer")));
		ui.click(new TreeItemLocator("Activator/Activator()", new ViewLocator(
				"org.eclipse.ui.views.ContentOutline")));

//		IWidgetLocator[] locs = ui.findAll(new TreeItemLocator("Activator.*", new
//				ViewLocator("org.eclipse.ui.views.ContentOutline")));
		
		
		IWidgetLocator[] locs = ui.findAll(new TreeItemLocator(".+", new
				ViewLocator("org.eclipse.ui.views.ContentOutline")));
		
//		IWidgetLocator[] locs = ui.findAll(new TreeItemLocator(".*", new
//				ViewLocator("org.eclipse.ui.views.ContentOutline")));
		
		System.out.println(locs.length);

		for (IWidgetLocator loc: locs){
			final Widget widget = (Widget) ((IWidgetReference)loc).getWidget();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (widget instanceof TreeItem)
						System.out.println(((TreeItem)widget).getText());
					
				}
			});
		}
		
//		ui.click(new MenuItemLocator("File/Exit"));
//		ui.wait(new ShellDisposedCondition("Eclipse SDK"));
	}
}
