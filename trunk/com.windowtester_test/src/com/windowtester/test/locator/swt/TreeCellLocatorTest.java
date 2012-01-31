package com.windowtester.test.locator.swt;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.locator.TreeCellLocator;
import com.windowtester.test.locator.swt.shells.TreeCellLocatorShell;

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
public class TreeCellLocatorTest extends AbstractLocatorTest {

	private Shell shell;
	private TreeCellLocatorShell locatorShell;

	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.AbstractLocatorTest#uiSetup()
	 */
	public void uiSetup() {
		shell = new Shell(Display.getDefault());
		shell.setLayout(new FillLayout());
		locatorShell = new TreeCellLocatorShell(shell);
		shell.open();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.AbstractLocatorTest#uiTearDown()
	 */
	@Override
	public void uiTearDown() {
		shell.close();
	}
	
	public void testChildSelectionColumn1() throws Exception {
		getUI().click(treeCell("0.0.2/0.0.2.1").at(column(1)));
		System.out.println(UIProxy.getToString(locatorShell.lastSelectedItem));
		System.out.println(locatorShell.lastSelectedItemColumn);
		assertEquals("TreeItem {0.0.2.1}", UIProxy.getToString(locatorShell.lastSelectedItem));
		assertEquals(1, locatorShell.lastSelectedItemColumn);
		getUI().pause(3000);
	}
	
	public void testChildSelectionColumn0() throws Exception {
		getUI().click(treeCell("0.0.4/0.0.4.2").at(column(0)));
		System.out.println(UIProxy.getToString(locatorShell.lastSelectedItem));
		System.out.println(locatorShell.lastSelectedItemColumn);
		assertEquals("TreeItem {0.0.4.2}", UIProxy.getToString(locatorShell.lastSelectedItem));
		assertEquals(0, locatorShell.lastSelectedItemColumn);
		getUI().pause(3000);
	}
	
	
	
	public void testHasText() throws Exception {
		IUIContext ui = getUI();
		for (int i=1; i < 10; ++i) {
			//System.out.println(i);
			ui.assertThat(treeCell("0.0." + i).at(column(1)).hasText("1.0." + i));
		}
	}
	
	
	public static TreeCellLocator treeCell(String path) {
		return new TreeCellLocator(path);
	}
	
	public static TreeCellLocator.Column column(int index) {
		return new TreeCellLocator.Column(index);
	}
	
	
}
