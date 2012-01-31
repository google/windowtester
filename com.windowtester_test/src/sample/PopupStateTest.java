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
package sample;

import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.selector.UIDriver;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.helpers.SimpleProjectHelper;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper.View;

public class PopupStateTest extends UITestCaseSWT {

	private String projectName;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUIContext ui = getUI();
        projectName = getClass().getSimpleName() + "Project"; 
        SimpleProjectHelper.createSimpleProject(ui, projectName);
        WorkBenchHelper.openView(ui, View.BASIC_NAVIGATOR);
	}
	
	public void testMenuItemStatus() throws Exception {
		IUIContext ui = getUI();
		TreeItem item = findItem(ui);
		verifyEnablement(ui, item);
	}

	private TreeItem findItem(IUIContext ui) throws WidgetSearchException {
        IWidgetReference itemRef = (IWidgetReference) ui.find(new TreeItemLocator(projectName, new ViewLocator(View.BASIC_NAVIGATOR.getViewID()))); //some criteria to find the item
        return (TreeItem) itemRef.getWidget();
	}

	private void verifyEnablement(IUIContext ui, TreeItem item)
			throws WaitTimedOutException {
		contextClick(item);
		// give it a few seconds to pop up... (a condition would be better)
		ui.pause(3000);
		// assert that the delete menu item is enabled
		ui.assertThat(new MenuItemLocator("Delete").isEnabled());

		ui.keyClick(WT.ESC); // dismiss menu
	}

	// Notice these warnings: not really API for this...
	@SuppressWarnings( { "restriction", "deprecation" })
	private void contextClick(Widget widget) {
		new UIDriver().click(widget, WT.BUTTON3);
	}

}
