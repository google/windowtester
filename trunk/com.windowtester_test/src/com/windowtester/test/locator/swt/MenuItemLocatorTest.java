package com.windowtester.test.locator.swt;

import static com.windowtester.runtime.swt.locator.SWTLocators.menuItem;

import java.util.Arrays;

import abbot.Platform;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.util.PathStringTokenizerUtil;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.test.locator.swt.shells.MenuTestShell;
import com.windowtester.test.util.junit.OS;
import com.windowtester.test.util.junit.RunOn;

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
public class MenuItemLocatorTest extends AbstractLocatorTest {

	private static final String RUN_PATTERN = "Run...";

	private static final String RUN_MENU = "Run";
	
	MenuTestShell window;
	
	 
	@Override
	public void uiSetup() {
		window = new MenuTestShell();
		window.open();
	} 
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	/*
	 * This is a corner-case test (top level menu items with no children).
	 */
	@RunOn(OS.WIN)
	public void testSelectionWithNoChildren() throws WidgetSearchException {
		IUIContext ui = getUI();
		//assert initial state
		assertNull(window.selectedMenuItem);
		
		//ui.click(window.topMenuItem, "top");
		ui.click(new MenuItemLocator("top"));
		// Mac and Linux do not deliver selection events until terminal item selected (should it?)
		if (!Platform.isOSX() && !Platform.isLinux())
			assertEquals(window.topMenuItem, window.selectedMenuItem);		
	}

	public void testSelection() throws Exception {

		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("parent/child 1"));
//		assertEquals(window.child1MenuItem, window.selectedMenuItem);
		ui.assertThat(new ICondition() {
			public boolean test() {
				return window.child1MenuItem.equals(window.selectedMenuItem);
			}
		});
		//notice we are escaping the '\' in grand/child
		ui.click(new MenuItemLocator("parent/child 2/grand\\/child"));
//		assertEquals(window.grandchildMenuItem, window.selectedMenuItem);
		ui.assertThat(new ICondition() {
			public boolean test() {
				return window.grandchildMenuItem.equals(window.selectedMenuItem);
			}
		});
		System.out.println("Click Mix/Image");
		ui.click(new MenuItemLocator("Mix/Image"));
		System.out.println("Click Mix/Normal");
		ui.click(new MenuItemLocator("Mix/Normal"));
	}
	
	
	public void testPathologicalEscapeSelection() throws Exception {
		System.out.println(Arrays.toString(PathStringTokenizerUtil.tokenize("parent/child 2/grand\\/children...")));
		//fail();
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("parent/child 2/grand\\/children...")); //&children...\t\tCtrl+F
//		assertEquals(window.grandchildrenMenuItem, window.selectedMenuItem);
		ui.assertThat(new ICondition() {
			public boolean test() {
				return window.grandchildrenMenuItem.equals(window.selectedMenuItem);
			}
		});
	}	
	
	public void testFailedSelections() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		
		//1
		try {
			ui.click(new MenuItemLocator("parent/child 2/nonexistent"));
			//ui.click(window.parentMenuItem_1, "child 2/nonexistent");
			fail("should have thrown WNF ex");
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			//pass
		} 
		//2
		try {
			ui.click(new MenuItemLocator("parent/bogus"));
			//ui.click(window.parentMenuItem_1, "bogus");
			fail("should have thrown WNF ex");
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			//pass
		}
		//3
		try {
			ui.click(new MenuItemLocator("bogus/really"));
			//ui.click(window.parentMenuItem_1, "bogus/really");
			fail("should have thrown WNF ex");
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			//pass
		}
		//4
		try {
			ui.click(new MenuItemLocator("bogus/really/bogus"));
			//ui.click(window.parentMenuItem_1, "bogus/really/bogus");
			fail("should have thrown WNF ex");
		} catch (com.windowtester.runtime.WidgetNotFoundException e) {
			//pass
		}
	}
	
	public void testRunPatternMatching() throws Exception {
		IUIContext ui = getUI();
		ui.click(menuItem(RUN_MENU + "/" + RUN_PATTERN));
	}
	//https://fogbugz.instantiations.com/fogbugz/default.asp?45779
	public void testFind() throws Exception {
		IUIContext ui = getUI();
		IWidgetLocator[] menus = ui.findAll(new MenuItemLocator(".*"));
		
		assertEquals(4, menus.length);
	}
	
	
}
