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

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.locator.JTreeItemLocator;

public class SwingTreeTest extends UITestCaseSwing {

	/**
	 * Create an Instance
	 */
//	public SwingTreeTest() {
//		super(swing.samples.SwingTree.class);
//	}

	/**
	 * Main test method.
	 */
	public void testMain() throws Exception {
		IUIContext ui = getUI();
		JTreeItemLocator treeItemLocator = new JTreeItemLocator("Root/Parent4", new SwingWidgetLocator(
				JViewport.class, new SwingWidgetLocator(JScrollPane.class,"scrollPane1")));
		ui.click(2, treeItemLocator);
		ui.assertThat(treeItemLocator.isSelected());
		ui.click(2, new JTreeItemLocator("Root/Parent4/Child41",
				new SwingWidgetLocator(JViewport.class, new SwingWidgetLocator(
						JScrollPane.class, "scrollPane1"))));
		
		JTreeItemLocator locator2 = new JTreeItemLocator("Root/Parent4/Child41/grandChild411",
				new SwingWidgetLocator(JViewport.class, new SwingWidgetLocator(
						JScrollPane.class, "scrollPane1")));
		ui.click(locator2);
		ui.assertThat(locator2.isSelected());
		
		ui.click(new JTreeItemLocator("Root/Item 4", new SwingWidgetLocator(
				JViewport.class, new SwingWidgetLocator(JScrollPane.class,
						"scrollPane2"))));
		ui.click(2, new JTreeItemLocator("Root/Item 0", new SwingWidgetLocator(
				JViewport.class, new SwingWidgetLocator(JScrollPane.class,
						"scrollPane2"))));

		JTreeItemLocator locator3 = new JTreeItemLocator("Root/Item 0/Node 01", new SwingWidgetLocator(
				JViewport.class, new SwingWidgetLocator(JScrollPane.class,"scrollPane2")));
		ui.click(locator3);
		ui.assertThat(locator3.isSelected());
	}

}