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
package context2.testcases;

import java.awt.Point;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import abbot.finder.ComponentNotFoundException;
import abbot.finder.MultipleComponentsFoundException;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.locator.JMenuItemLocator;
import com.windowtester.runtime.swing.locator.JTableItemLocator;

public class JTableTest2 extends UITestCaseSwing {

	
	JTable table;
	TableModel model;
	
	
	private IUIContext ui;
	/**
	 * Create an Instance
	 */
//	public JTableTest2() {
//		super(swing.samples.SimpleTable.class);
//	}
	
	protected void setUp() throws Exception {
		ui = getUI();
	}
	
/*	public void testDoubleClicks() throws ComponentNotFoundException, MultipleComponentsFoundException, WidgetSearchException {
		
		IWidgetReference locator;
		
		
		
		locator = (IWidgetReference)ui.click(2,new JTableItemLocator(new Point(1,0)));
		
		table = (JTable)locator.getWidget();
		model = table.getModel();
		
		ui.enterText("next\n");
		
		ui.click(new JTableItemLocator(new Point(1,0)));
		assertEquals(model.getValueAt(table.getSelectedRow(),table.getSelectedColumn()),"next");
		
	//	try {
	//		ui.click("five");
	//	} catch (LocationUnavailableException e) {
	//		//pass
	//	} 
	}
	
*/	
	
	public void testContextMenuSelection() throws ComponentNotFoundException, MultipleComponentsFoundException, WidgetSearchException {

		
		ui.click(new JTableItemLocator(new Point(2,1)));
		ui.contextClick(new JTableItemLocator(new Point(2,1)),new JMenuItemLocator("choice3"));
		
		ui.contextClick(new JTableItemLocator(new Point(0,2)), new JMenuItemLocator("choice1"));
		
	}
	
	
}
