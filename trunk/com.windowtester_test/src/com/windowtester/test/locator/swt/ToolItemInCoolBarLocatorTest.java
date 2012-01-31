package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.ToolItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.widgets.ToolItemReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.test.locator.swt.shells.CoolBarTestShell;

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
public class ToolItemInCoolBarLocatorTest extends AbstractLocatorTest {

	
	CoolBarTestShell _window;
	
	 
	@Override
	public void uiSetup() {
		_window = new CoolBarTestShell();
		_window.open();
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}	
	
	public void testRefSelection() throws WidgetSearchException {
		IUIContext ui = getUI();
		assertNull(_window.getLastSelection());
		for (int i=0; i < 5; ++i) {
			ui.click(WidgetReference.create(_window.getItems()[i]));
			assertEquals(_window.getItems()[i], _window.getLastSelection());
		}
	}
	
	public void testSelection() throws WidgetSearchException {
		IUIContext ui = getUI();
		assertNull(_window.getLastSelection());
		for (int i=0; i < 5; ++i) {
			ui.click(locator(_window.getItems()[i]));
			assertEquals(_window.getItems()[i], _window.getLastSelection());
		}
	}

	private ILocator locator(ToolItem item) {
		return new SWTWidgetLocator(ToolItem.class, new ToolItemReference(item).getText());
	}	
	
	
}
