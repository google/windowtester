package com.windowtester.test.locator.swt;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import abbot.tester.swt.ItemTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.test.locator.swt.shells.CTabItemTestShell;
import com.windowtester.test.util.Serializer;

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
public class CTabItemLocatorTest extends AbstractLocatorTest {

	
	CTabItemTestShell _window;
	
	 
	@Override
	public void uiSetup() {
		_window = new CTabItemTestShell();
		_window.open();
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	public void testSelectionWithRefs() throws WidgetSearchException {

		IUIContext ui = getUI();
		
		CTabFolder folder = _window.getCTabFolder();
		CTabItem[] items = folder.getItems();

		//assert initial state
		assertNull(folder.getSelection());
		
		ui.click(new WidgetReference(items[0]));
		assertEquals(items[0], folder.getSelection());
		
		ui.click(new WidgetReference(items[1]));
		assertEquals(items[1], folder.getSelection());
		
		ui.click(new WidgetReference(items[2]));
		assertEquals(items[2], folder.getSelection());

	}

	
	
	public void testSelection() throws WidgetSearchException {

		IUIContext ui = getUI();
		
		CTabFolder folder = _window.getCTabFolder();
		CTabItem[] items = folder.getItems();

		//assert initial state
		assertNull(folder.getSelection());
		
		ui.click(locator(items[0]));
		assertEquals(items[0], folder.getSelection());
		
		ui.click(locator(items[1]));
		assertEquals(items[1], folder.getSelection());
		
		ui.click(locator(items[2]));
		assertEquals(items[2], folder.getSelection());

	}

	public void testStreamOutAndIn() throws Exception {
		Serializer.serializeOutAndIn(new CTabItemLocator("Tab"));
	}
	
	private ILocator locator(CTabItem tabItem) {
		return new CTabItemLocator(new ItemTester().getText(tabItem));
	}	
	
	public void testClose() throws Exception {
		IUIContext ui = getUI();
		ui.ensureThat(new CTabItemLocator("eins").isClosed());
		ui.assertThat(new CTabItemLocator("eins").isVisible(false));
	}
	
	
//	/**
//	 * Adapted from:
//	 * com.windowtester.test.gef.tests.common.AbstractGEFDrivingTest.closeWelcomePageIfNecessary()
//	 */
//	public void XtestCloseRef() throws Exception {
//		IUIContext ui = getUI();
//		IWidgetLocator[] tab = ui.findAll(
//				new CTabItemLocator("eins"));
//		ui.close(tab[0]);
//		ui.assertThat(new CTabItemLocator("eins").isVisible(false));
//	}
	

	
}
