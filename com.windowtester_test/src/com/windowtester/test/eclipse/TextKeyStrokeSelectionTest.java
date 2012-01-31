package com.windowtester.test.eclipse;

import org.eclipse.swt.widgets.Text;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.sandbox.views.SandboxViews;

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
public class TextKeyStrokeSelectionTest extends BaseTest {

	/**
	 * Main test method.
	 */
	public void testarrowDelete() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Other..."));
		ui.wait(new ShellShowingCondition("Show View"));
		ui.click(new FilteredTreeItemLocator(SandboxViews.TEXT_VIEW_PATH));
		
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Show View"));
		ui.click(new NamedWidgetLocator("text1"));
		ui.keyClick(WT.HOME);
		ui.keyClick(WT.SHIFT|WT.END);
		ui.keyClick(WT.DEL);
		ui.enterText("test");
		
		IWidgetReference ref = (IWidgetReference) ui.find(new NamedWidgetLocator("text1"));
		Text text = (Text) ref.getWidget();
		String contents = UIProxy.getText(text);
		assertEquals("test", contents);

	}

}