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
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.WidgetPrinter;
import com.windowtester.runtime.swt.locator.CComboItemLocator;
import com.windowtester.runtime.swt.locator.TableCellLocator;
import com.windowtester.runtime.swt.util.DebugHelper;
import com.windowtester.test.locator.swt.shells.TableCellTestShell;

public class TableCellLocatorTest extends AbstractLocatorTest {

	TableCellTestShell window;
	
	@Override
	public void uiSetup() {
		window = new TableCellTestShell();
		window.open();
	} 
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	
	public void testTableCell() throws WidgetSearchException{
		IWidgetReference wref;
		TableItem item;
		
		IUIContext ui =  getUI();
		
		new DebugHelper().printWidgets();
		
		WidgetPrinter printer = new WidgetPrinter();
		DisplayReference.getDefault().getActiveShell().accept(printer);
		System.out.println(printer.asString());
		//fail();
		
		
		// click on column header
		ui.assertThat(new TableCellLocator(0,1).hasText("Name"));
		
		// click on column 2 header
		ui.assertThat(new TableCellLocator(0, 2).hasText("Value"));
		
		
		// row no, col no
		wref = (IWidgetReference)ui.click(new TableCellLocator(1,2));
		item = (TableItem)wref.getWidget();
		ui.click(new CComboItemLocator("zzz"));
		ui.keyClick(WT.TAB);
		assertTextEquals(item, 1, "zzz");
		
		
		
		// row no, col name
//		wref = (IWidgetReference)ui.click(new TableCellLocator(2,"Value"));
//		item = (TableItem)wref.getWidget();
//		ui.click(new CComboItemLocator("xxx"));
//		assertTextEquals(item, 1, "xxx");
		
		
		
		
		// row text, col no
		wref = (IWidgetReference)ui.click(new TableCellLocator("item 1",1));
		item = (TableItem)wref.getWidget();
		ui.enterText("Item new");
//		wref = (IWidgetReference)ui.click(new TableCellLocator("Item new",1));
//		item = (TableItem)wref.getWidget();
		
		
//		assertEquals("Item new",getText(item,0));
		
		// row text, column text
		wref = (IWidgetReference)ui.click(new TableCellLocator("item 2","Name"));
		item = (TableItem)wref.getWidget();
		ui.enterText("Item no 5");
//		assertEquals("Item no 5",getText(item,0));
		
		ui.click(new TableCellLocator("Item new",1));
		
		// TODO Does not work.... need to implement sometime
		if (false)
			ui.contextClick(new TableCellLocator("Item new",1), "Copy");
	}
	
	//http://fogbugz.instantiations.com/default.php?36805
	public void testHasTextByRowColNames() throws Exception {
		IWidgetReference wref;
		TableItem item;
		
		IUIContext ui =  getUI();
		
		// click on column header
		ui.assertThat(new TableCellLocator(0,1).hasText("Name"));
		
		// click on column 2 header
		ui.assertThat(new TableCellLocator(0, 2).hasText("Value"));
		
		
		wref = (IWidgetReference)ui.click(new TableCellLocator("item 1", "Value"));
		item = (TableItem)wref.getWidget();
		ui.click(new CComboItemLocator("yyy"));
		ui.keyClick(WT.TAB);
		assertTextEquals(item, 1, "yyy");
		
		
		ui.assertThat(new TableCellLocator("item 1", "Value").hasText("yyy"));
		ui.assertThat(new HasTextCondition(new TableCellLocator("item 1", "Value"), "yyy"));

		assertEquals("yyy", new TableCellLocator("item 1", "Value").getText(ui));
		
		
		
	}
	
	
	private void assertTextEquals(final TableItem item, final int column, final String expected) {
		getUI().assertThat(new ICondition() {
			/* (non-Javadoc)
			 * @see com.windowtester.runtime.condition.ICondition#test()
			 */
			public boolean test() {
				return expected.equals(getText(item, column));			
			}
		});
	}

	private String getText(TableItem item, int index){
		 String text = new TableItemTester().getText(item,index);
		 System.out.println(text);
		 return text;
	}
	
}
