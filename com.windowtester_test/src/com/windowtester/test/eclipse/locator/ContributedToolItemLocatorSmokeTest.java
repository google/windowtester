package com.windowtester.test.eclipse.locator;

import org.eclipse.swt.widgets.ToolItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.ToolItemReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.EclipseUtil;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper.Perspective;

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
public class ContributedToolItemLocatorSmokeTest extends BaseTest {

	public void XtestDiagnostic() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("&Window/Show &View/Navigator"));
		IWidgetLocator[] locators = ui.findAll(new SWTWidgetLocator(ToolItem.class));
		
		for (int i = 0; i < locators.length; i++) {
			ToolItem item = (ToolItem)((IWidgetReference)locators[i]).getWidget();
			System.out.print("data: ");
			System.out.println(new ToolItemReference(item).getData());
			System.out.print("associated contrib: ");
			System.out.println(ContributedToolItemLocator.getAssociatedContributionID(item));
		}
	}

	public void testOpenSearchToolItem() throws WidgetSearchException {
		IUIContext ui = getUI();
		WorkBenchHelper.openPerspective(getUI(), Perspective.JAVA);
		ui.click(new MenuItemLocator("&Window/Show &View/Navigator"));
		String id = EclipseUtil.isAtLeastVersion_34() ? "org.eclipse.search.OpenSearchDialogPage" : "org.eclipse.search.ui.openSearchDialog";
		ui.click(new ContributedToolItemLocator(id));
		ui.click(new ButtonLocator("Cancel"));
	}

}
