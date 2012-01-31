package com.windowtester.test.codegen;

import java.awt.Composite;
import java.awt.Point;

import javax.swing.JButton;

import junit.framework.TestCase;

import com.windowtester.codegen.generator.LocatorJavaStringFactory;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swing.locator.JButtonLocator;
import com.windowtester.runtime.swing.locator.JMenuItemLocator;
import com.windowtester.runtime.swing.locator.JTableItemLocator;
import com.windowtester.runtime.swing.locator.JTreeItemLocator;

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
public class LocatorJavaStringFactoryTest extends TestCase {

	public void testSwingBasic() {
		assertEquals("new JButtonLocator(\"OK\")", toString(new JButtonLocator("OK")));
		assertEquals("new JMenuItemLocator(\"path/to/item\")", toString(new JMenuItemLocator("path/to/item")));
		assertEquals("new JTreeItemLocator(\"path/to/item\")", toString(new JTreeItemLocator("path/to/item")));
		assertEquals("new SwingWidgetLocator(JButton.class, \"OK\")", toString(new SwingWidgetLocator(JButton.class, "OK")));
		assertEquals("new JTableItemLocator(new Point(2,3))", toString(new JTableItemLocator(new Point(2,3))));
	}
	
	public void testSwingNested() {
		assertEquals("new JButtonLocator(\"OK\", new SwingWidgetLocator(Composite.class))", toString(new JButtonLocator("OK", new SwingWidgetLocator(Composite.class))));
		assertEquals("new JTreeItemLocator(\"path/to/item\", new SwingWidgetLocator(Composite.class))", toString(new JTreeItemLocator("path/to/item", new SwingWidgetLocator(Composite.class))));
		assertEquals("new JTreeItemLocator(\"path/to/item\", 2, new SwingWidgetLocator(Composite.class))", toString(new JTreeItemLocator("path/to/item", 2, new SwingWidgetLocator(Composite.class))));
	}

	
	
//tested in com.windowtester.test.codegen.SWTAPICodeBlockBuilderTest	
//	public void testTableItemLocatorChecked() {
//		assertEquals("new TableItemLocator(WT.CHECK, \"item\")", new TableItemLocator(WT.CHECK, "item"));
//	}
//	
//	public void testTreeItemLocatorChecked() {
//		assertEquals("new TreeItemLocator(WT.CHECK, \"item\")", new TreeItemLocator(WT.CHECK, "item"));
//	}
	

	private String toString(IWidgetLocator locator) {
		return LocatorJavaStringFactory.toJavaString(locator);	
	}
	
	
}
