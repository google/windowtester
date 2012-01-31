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

import swing.samples.SwingTables;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JTableItemLocator;
import com.windowtester.runtime.swing.locator.NamedWidgetLocator;

public class NamedTableTest extends UITestCaseSwing {
	
	public NamedTableTest(){
		super(SwingTables.class);
	}
	
	public void testNamedTable () throws WidgetSearchException{
		IUIContext ui = getUI();
		ui.wait(new WindowShowingCondition("TableDemo2"));
		ui.click(new JTableItemLocator(new Point(1,1), new NamedWidgetLocator("table1")));
		ui.click(new JTableItemLocator(new Point(3,2), new NamedWidgetLocator("table1")));
		ui.click(new JTableItemLocator(new Point(1,0), new NamedWidgetLocator("table2")));
		ui.click(new JTableItemLocator(new Point(3,1), new NamedWidgetLocator("table2")));
	}

}
