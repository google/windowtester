package com.windowtester.test.locator.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;

import abbot.tester.swt.ListTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.locator.ListItemLocator;
import com.windowtester.test.locator.swt.shells.ListTestShell;


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
public class ListItemLocatorTest extends AbstractLocatorTest {

	ListTestShell _window;
	 
	@Override
	public void uiSetup() {
		_window = new ListTestShell();
		_window.open();
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	private List getList() {
		return _window.getList();
	}
	
//	public void testCtrlClicks() throws WidgetSearchException {
//		
//		IUIContext ui = getUI();
//		
//		List list = getList();
//		ui.click(new ListItemLocator("two", new WidgetReference(list)));
//		assertContainsExactly(new ListTester().getSelection(list), new String[]{"two"});
//		
//		ui.click(1, new ListItemLocator("seven", new WidgetReference(list)), SWT.BUTTON1 | SWT.MOD1);		
//		ui.click(1, new ListItemLocator("one", new WidgetReference(list)), SWT.BUTTON1 | SWT.MOD1);		
//		assertContainsExactly(new ListTester().getSelection(list), new String[]{"one", "two", "seven"});
//	}
	
	
	public void testCtrlClicks() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		
		List list = getList();
		ui.click(new ListItemLocator("two"));
		assertContainsExactly(new ListTester().getSelection(list), new String[]{"two"});
		
		ui.click(1, new ListItemLocator("seven"), SWT.BUTTON1 | SWT.MOD1);		
		ui.click(1, new ListItemLocator("one"), SWT.BUTTON1 | SWT.MOD1);		
		assertContainsExactly(new ListTester().getSelection(list), new String[]{"one", "two", "seven"});
	}
	
	public void testShiftClicks() throws WidgetSearchException {

		IUIContext ui = getUI();
		
		List list = getList();
		ui.click(new ListItemLocator("five"));

		assertContainsExactly(new ListTester().getSelection(list), new String[]{"five"});
		ui.click(1, new ListItemLocator("seven"), SWT.BUTTON1 | SWT.SHIFT);		
		assertContainsExactly(new ListTester().getSelection(list), new String[]{"five", "six", "seven"});
	}

	public void testRegularClicks() throws WidgetSearchException {

		IUIContext ui = getUI();
		
		List list = getList();
		
		assertContainsExactly(new ListTester().getSelection(list), new String[]{});
		ui.click(new ListItemLocator("one"));
		assertContainsExactly(new ListTester().getSelection(list), new String[]{"one"});
		
		ui.click(new ListItemLocator("two"));
		assertContainsExactly(new ListTester().getSelection(list), new String[]{"two"});
		
		ui.click(new ListItemLocator("seven"));
		assertContainsExactly(new ListTester().getSelection(list), new String[]{"seven"});
	}
	
	public void testIsVisible() throws Exception {
		// test for regression on Case 41970
		IUIContext ui = getUI();
		
		for (int i = 0; i < ListTestShell.LIST_TEST_SHELL_ITEMS.length; i++) {
			ui.assertThat(new ListItemLocator(ListTestShell.LIST_TEST_SHELL_ITEMS[i]).isVisible());
			ui.assertThat(new ListItemLocator(ListTestShell.LIST_TEST_SHELL_ITEMS[i]).isVisible(true));
			ui.assertThat(new ListItemLocator(ListTestShell.LIST_TEST_SHELL_ITEMS[i]+'_').isVisible(false));
		}
		// what was recorded
		//ui.click(new ListItemLocator("five"));
		//ui.assertThat(new ListItemLocator().isVisible());
		//ui.close(new SWTWidgetLocator(Shell.class, "List Test"));
	}
	
	public void testContextClicks() throws Exception{
		IUIContext ui = getUI();

		ui.click(new ListItemLocator("one"));
		ui.contextClick(new ListItemLocator("one"), "Menu item 1");

		//TODO: update test shell to show different menu items specific to the target item
		//ui.contextClick(new ListItemLocator("two"), "Menu item 2");
		
	}
	
	
//	public void testDiagnostic() {
//		fail("unimplemented");
//
//		List list = window.getList();
//		ListTester listTester = new ListTester();
//		Rectangle clientArea = listTester.getClientArea(list);
//		System.out.println("client area:\t" + clientArea);
//		System.out.println("client area/7:\t" + clientArea.height/7);
//		System.out.println("item height:\t" + UIProxy.getItemHeight(list));
//	}
	

	
}
