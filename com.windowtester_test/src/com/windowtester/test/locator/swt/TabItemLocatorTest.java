package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import abbot.tester.swt.ItemTester;
import abbot.tester.swt.TabFolderTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.locator.TabItemLocator;
import com.windowtester.test.locator.swt.shells.TabItemTestShell;


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
public class TabItemLocatorTest extends AbstractLocatorTest {

	
	TabItemTestShell _window;
	
	 
	@Override
	public void uiSetup() {
		_window = new TabItemTestShell();
		_window.open();
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	
	public void testWithRefs() throws WidgetSearchException {

		IUIContext ui = getUI();
		
		TabFolder folder = _window.getTabFolder();
		TabItem[] items = new TabFolderTester().getItems(folder);
		
		//assert initial state
		assertContainsExactly(new TabFolderTester().getSelection(folder), new TabItem[]{items[0]});
		
		ui.click(reference(items[1]));
		assertContainsExactly(new TabFolderTester().getSelection(folder), new TabItem[]{items[1]});
		
		
		ui.click(reference(items[2]));
		assertContainsExactly(new TabFolderTester().getSelection(folder), new TabItem[]{items[2]});
		
		
		ui.click(reference(items[0]));
		assertContainsExactly(new TabFolderTester().getSelection(folder), new TabItem[]{items[0]});
	}

	public void testSelection() throws WidgetSearchException {

		IUIContext ui = getUI();
		
		TabFolder folder = _window.getTabFolder();
		TabItem[] items = new TabFolderTester().getItems(folder);
		
		//assert initial state
		assertContainsExactly(new TabFolderTester().getSelection(folder), new TabItem[]{items[0]});
		
		ui.click(locator(items[1]));
		assertContainsExactly(new TabFolderTester().getSelection(folder), new TabItem[]{items[1]});
		
		
		ui.click(reference(items[2]));
		assertContainsExactly(new TabFolderTester().getSelection(folder), new TabItem[]{items[2]});
		
		
		ui.click(reference(items[0]));
		assertContainsExactly(new TabFolderTester().getSelection(folder), new TabItem[]{items[0]});
	}
	
	
	private ILocator locator(TabItem tabItem) {
		return new TabItemLocator(new ItemTester().getText(tabItem));
	}

	private WidgetReference reference(TabItem item) {
		return new WidgetReference(item);
	}
	
}
