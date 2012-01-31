package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import abbot.tester.swt.ItemTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.ToolBarLocator;
import com.windowtester.runtime.swt.locator.ToolItemLocator;
import com.windowtester.runtime.swt.util.DebugHelper;
import com.windowtester.test.locator.swt.shells.ToolItemShell2;

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

public class ToolItemLocatorTest2 extends AbstractLocatorTest {

	
	ToolItemShell2 window;
	
	 
	@Override
	public void uiSetup() {
		window = new ToolItemShell2(Display.getDefault());
		window.open();
	} 
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	public void testFindAll() throws WidgetSearchException {
		IUIContext ui = getUI();
		IWidgetLocator[] itemRefs = ui.findAll(new ToolItemLocator(".*"));		
		assertEquals(4, itemRefs.length);
	}

	
	@SuppressWarnings("unchecked")
	public void testFindNamed() throws Exception {
		IUIContext ui = getUI();
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				window.itemA.setData("name", "named.item");
			}
		});
		
		IWidgetLocator[] itemRefs = ui.findAll(new ToolItemLocator(".*").named("named.item"));		
		assertEquals(1, itemRefs.length);
		assertEquals(window.itemA, ((IWidgetReference)itemRefs[0]).getWidget());
	}
	
	@SuppressWarnings("unchecked")
	public void testFindFirstToolItems() throws Exception {
		IUIContext ui = getUI();
		final IWidgetLocator[] itemRefs = ui.findAll(new ToolItemLocator(".*").in(0, new ToolBarLocator()));		
		assertEquals(2, itemRefs.length);
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				assertEquals(window.item1, ((IWidgetReference)itemRefs[0]).getWidget());
				assertEquals(window.itemA, ((IWidgetReference)itemRefs[1]).getWidget());				
			}
		});
	}
	
	public void testFindToolItemsInFirstToolBar() throws Exception {
		IUIContext ui = getUI();
		new DebugHelper().printWidgets();
		final IWidgetLocator[] itemRefs = ui.findAll(new ToolItemLocator(".*").in(new ToolBarLocator().in(0, new SWTWidgetLocator(ToolItemShell2.class))));		
		assertEquals(2, itemRefs.length);
	}
	
	public void testFindToolItemByText() throws Exception {
		IUIContext ui = getUI();
		new DebugHelper().printWidgets();
		final IWidgetLocator itemRef = ui.find(new ToolItemLocator("Item 1"));	
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				assertEquals(window.item1, ((IWidgetReference)itemRef).getWidget());
			}
		});
	}
	
	public void testFindFirstToolBar() throws Exception {
		IUIContext ui = getUI();
		new DebugHelper().printWidgets();
		final IWidgetLocator[] itemRefs = ui.findAll(new ToolBarLocator().in(0, new SWTWidgetLocator(ToolItemShell2.class)));		
		assertEquals(1, itemRefs.length);
		assertEquals(window.toolBar1, ((IWidgetReference)itemRefs[0]).getWidget());
	}
	
	
	public void testFindAllToolBars() throws Exception {
		IUIContext ui = getUI();
		final IWidgetLocator[] itemRefs = ui.findAll(new ToolBarLocator());		
		assertEquals(2, itemRefs.length);
	}
	
	
	public void testRawLocatorFindToolItemsInFirstToolBar() throws Exception {
		IUIContext ui = getUI();
		final IWidgetLocator[] itemRefs = ui.findAll(new SWTWidgetLocator(ToolItem.class, new SWTWidgetLocator(ToolBar.class, 0, new SWTWidgetLocator(ToolItemShell2.class))));
		assertEquals(2, itemRefs.length);
	}
	
	public void testRawMixFindToolItemsInFirstToolBar() throws Exception {
		IUIContext ui = getUI();
		final IWidgetLocator[] itemRefs = ui.findAll(new SWTWidgetLocator(ToolItem.class, new SWTWidgetLocator(ToolBar.class).in(0, new SWTWidgetLocator(ToolItemShell2.class))));
		assertEquals(2, itemRefs.length);
	}
	
	public void testRawMixFindFirstToolItems() throws Exception {
		IUIContext ui = getUI();
		final IWidgetLocator[] itemRefs = ui.findAll(new SWTWidgetLocator(ToolItem.class, 0, new SWTWidgetLocator(ToolBar.class)).in(new SWTWidgetLocator(ToolItemShell2.class)));
		assertEquals(2, itemRefs.length);
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				assertEquals(window.item1, ((IWidgetReference)itemRefs[0]).getWidget());
				assertEquals(window.itemA, ((IWidgetReference)itemRefs[1]).getWidget());				
			}
		});
	}
	
	
	
//	public void testSelectionWithRefs() throws WidgetSearchException {
//		
//		IUIContext ui = getUI();
//		
//		assertNull(window.lastSelection);
//		
//		ui.click(reference(window.itemA));
//		assertEquals(window.itemA, window.lastSelection);
//		
//		ui.click(reference(window.itemB));
//		assertEquals(window.itemB, window.lastSelection);
//		
//		ui.click(reference(window.itemC));
//		assertEquals(window.itemC, window.lastSelection);
//		
//		ui.click(reference(window.itemA));
//		assertEquals(window.itemA, window.lastSelection);
//	}
//
//	
//	public void testSelection() throws WidgetSearchException {
//		
//		IUIContext ui = getUI();
//		
//		assertNull(window.lastSelection);
//		 
//		ui.click(locator(window.itemA));
//		assertEquals(window.itemA, window.lastSelection);
//		
//		ui.click(locator(window.itemB));
//		assertEquals(window.itemB, window.lastSelection);
//		
//		ui.click(locator(window.itemC));
//		assertEquals(window.itemC, window.lastSelection);
//		
//		ui.click(locator(window.itemA));
//		assertEquals(window.itemA, window.lastSelection);
//	}
	
	
//	public void testNamedItemEnablement() throws Exception {
//		Display.getDefault().syncExec(new Runnable(){
//			public void run() {
//				window.itemA.setData("name", "named.item");
//			}
//		});
//		getUI().assertThat(new NamedWidgetLocator("named.item").isEnabled());
//	}
	
	
	private ILocator locator(ToolItem item) {
		return new SWTWidgetLocator(ToolItem.class, new ItemTester().getText(item));
	}

	private ILocator reference(ToolItem item) {
		return new WidgetReference(item);
	}

}
