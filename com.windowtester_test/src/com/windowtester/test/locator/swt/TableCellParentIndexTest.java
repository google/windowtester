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
package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.TableItem;

import abbot.tester.swt.TableItemTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.locator.ShellLocator;
import com.windowtester.runtime.swt.locator.TableCellLocator;
import com.windowtester.test.locator.swt.shells.TableCellParentIndexTestShell;

/**
 * Test for TableCellLocator with parent and index
 * 
 * @author Keerti P
 *
 */
public class TableCellParentIndexTest extends AbstractLocatorTest {

	TableCellParentIndexTestShell _window;
	
	@Override
	public void uiSetup() {
		_window = new TableCellParentIndexTestShell();
		_window.open();
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	
	public void testTableCellParentIndex() throws WidgetSearchException{
		IWidgetReference wref;
		TableItem item;
		
		IUIContext ui =  getUI();
		wref = (IWidgetReference)ui.click(new TableCellLocator(3,1).in(0,new ShellLocator("TableCell Example",false)));
		item = (TableItem)wref.getWidget();
		assertEquals("Item 2", getText(item,0));
		
		wref = (IWidgetReference)ui.click(new TableCellLocator(2,1).in(1,new ShellLocator("TableCell Example",false)));
		item = (TableItem)wref.getWidget();
		assertEquals("Item 1", getText(item,0));
		
	}
	
	private String getText(TableItem item, int index){
		 String text = new TableItemTester().getText(item,index);
		 return text;
	}
}