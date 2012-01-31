package com.windowtester.test.eclipse.identifier;

import static com.windowtester.runtime.swt.locator.SWTLocators.button;
import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.openPreferencePage;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.locator.WidgetIdentifier;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.test.eclipse.BaseTest;

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
@SuppressWarnings("restriction")
public class LabeledButtonIdentificationTest extends BaseTest {

	
	public void testLocateButtons() throws Exception {
		IUIContext ui = getUI();
		openPreferencePage(ui, "Java/Editor/Syntax Coloring");
		IWidgetLocator[] locators = ui.findAll(button(".*"));
		for (IWidgetLocator loc : locators) {
			Widget w = (Widget) ((IWidgetReference)loc).getWidget();
			System.out.print(UIProxy.getToString(w) + " -> ");
			IWidgetIdentifier locator = WidgetIdentifier.getInstance().identify(w);
			System.out.println(locator);
			assertTrue(locator instanceof ButtonLocator);
		}
	}
	
}
