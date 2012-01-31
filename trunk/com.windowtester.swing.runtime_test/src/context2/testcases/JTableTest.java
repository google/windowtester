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
import java.awt.event.InputEvent;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JTableItemLocator;

public class JTableTest extends UITestCaseSwing {
	
	
	JTable table;
	TableModel model;

	private IUIContext ui;
	
	
	/**
	 * Create an Instance
	 */
	public JTableTest() {
		super(swing.samples.SimpleTable.class);
	}
	
	protected void setUp() throws Exception {
		ui = getUI();
	}
	

	
public void testClicks() throws WidgetSearchException {
		
		IWidgetLocator locator;
		
		ui.wait(new WindowShowingCondition("TableDemo"));
		locator = ui.click(new JTableItemLocator(new Point(0,0)));
		table = (JTable)((IWidgetReference)locator).getWidget();
		model = table.getModel();
		ui.assertThat(new JTableItemLocator(new Point(0,0)).isSelected());
		assertEquals(1,table.getSelectedRowCount());
		
		locator = ui.click(1,new JTableItemLocator(new Point(2,0)),InputEvent.BUTTON1_MASK | InputEvent.SHIFT_MASK);
		table = (JTable)((IWidgetReference)locator).getWidget();
		model = table.getModel();
		assertEquals(3,table.getSelectedRowCount());
		ui.assertThat(new JTableItemLocator(new Point(2,0)).isSelected());
		ui.assertThat(new JTableItemLocator(new Point(1,0)).isSelected());
		ui.assertThat(new JTableItemLocator(new Point(0,0)).isSelected());
		
		locator = ui.click(new JTableItemLocator(new Point(0,0)));
		table = (JTable)((IWidgetReference)locator).getWidget();
		model = table.getModel();
		
		assertEquals(1,table.getSelectedRowCount());
		
		ui.click(1,new JTableItemLocator(new Point(2,0)),InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);
		assertEquals(table.getSelectedRowCount(),2);
		ui.assertThat(new JTableItemLocator(new Point(1,0)).isSelected(false));
//	}
	
	
	
//	public void testClicks() throws WidgetSearchException {
	
//		IWidgetLocator locator;
		
		
		locator = ui.click(new JTableItemLocator(new Point(0,1)));
		table = (JTable)((IWidgetReference)locator).getWidget();
		model = table.getModel();
	//	System.out.println(table.getSelectedRow());
	//	System.out.println(table.getSelectedColumn());
		assertEquals(model.getValueAt(table.getSelectedRow(),table.getSelectedColumn()),"two");
		
		ui.click(new JTableItemLocator(new Point(1,3)));
		assertEquals(model.getValueAt(table.getSelectedRow(),table.getSelectedColumn()),"eight");
		
		ui.click(new JTableItemLocator(new Point(5,1)));
		assertEquals(model.getValueAt(table.getSelectedRow(),table.getSelectedColumn()),"twenty-two");
		
		ui.click(new JTableItemLocator(new Point(0,2)));
		assertEquals(model.getValueAt(table.getSelectedRow(),table.getSelectedColumn()),"three");	
//		ui.click(new JMenuItemLocator("File/Exit"));
		
//	}
	

	
//	public void testCtrlClicks() throws WidgetSearchException {
	
//		IWidgetLocator locator;
//		JTable table;
//		TableModel model;
		
		locator = ui.click(new JTableItemLocator(new Point(0,0)));
		table = (JTable)((IWidgetReference)locator).getWidget();
		model = table.getModel();
		ui.assertThat(new JTableItemLocator(new Point(0,0)).isSelected());
		assertEquals(1,table.getSelectedRowCount());
		
		ui.click(1,new JTableItemLocator(new Point(2,0)),InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);
		ui.assertThat(new JTableItemLocator(new Point(0,0)).isSelected());
		ui.assertThat(new JTableItemLocator(new Point(2,0)).isSelected());
		assertEquals(table.getSelectedRowCount(),2);
	}
	

}
