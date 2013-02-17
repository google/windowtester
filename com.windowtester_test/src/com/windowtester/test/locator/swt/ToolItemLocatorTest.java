package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.ToolItem;

import abbot.tester.swt.ItemTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.test.locator.swt.shells.ToolBarTestShell;

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

public class ToolItemLocatorTest extends AbstractLocatorTest {

	
	ToolBarTestShell window;
	
	 
	@Override
	public void uiSetup() {
		window = new ToolBarTestShell();
		window.open();
	} 
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	public void testSelectionWithRefs() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		
		assertNull(window.lastSelection);
		
		ui.click(reference(window.itemA));
		assertEquals(window.itemA, window.lastSelection);
		
		ui.click(reference(window.itemB));
		assertEquals(window.itemB, window.lastSelection);
		
		ui.click(reference(window.itemC));
		assertEquals(window.itemC, window.lastSelection);
		
		ui.click(reference(window.itemA));
		assertEquals(window.itemA, window.lastSelection);
	}

	
	public void testSelection() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		
		assertNull(window.lastSelection);
		 
		ui.click(locator(window.itemA));
		assertEquals(window.itemA, window.lastSelection);
		
		ui.click(locator(window.itemB));
		assertEquals(window.itemB, window.lastSelection);
		
		ui.click(locator(window.itemC));
		assertEquals(window.itemC, window.lastSelection);
		
		ui.click(locator(window.itemA));
		assertEquals(window.itemA, window.lastSelection);
	}
	
	
	public void testNamedItemEnablement() throws Exception {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				window.itemA.setData("name", "named.item");
			}
		});
		getUI().assertThat(new NamedWidgetLocator("named.item").isEnabled());
	}
	
	
	private ILocator locator(ToolItem item) {
		return new SWTWidgetLocator(ToolItem.class, new ItemTester().getText(item));
	}

	private ILocator reference(ToolItem item) {
		return new WidgetReference(item);
	}

}
