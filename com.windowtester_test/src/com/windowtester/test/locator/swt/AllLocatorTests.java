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

import com.windowtester.test.util.junit.ManagedSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllLocatorTests {

	public static Test suite() {
		TestSuite suite = new ManagedSuite("Test for com.windowtester.test.locator.swt");
		//$JUnit-BEGIN$
		suite.addTestSuite(ButtonLocatorTest.class);
		suite.addTestSuite(CComboLocatorTest.class);
		suite.addTestSuite(CTabItemLocatorTest.class);
		suite.addTestSuite(ComboLocatorTest.class);
		suite.addTestSuite(ContributedToolItemLocatorTest.class);
		suite.addTestSuite(LinkSelectionTest.class);
		suite.addTestSuite(ListItemLocatorTest.class);
		suite.addTestSuite(MenuItemLocatorTest.class);
		suite.addTestSuite(NamedWidgetLocatorTest.class);
		suite.addTestSuite(NamedWidgetLocatorTest2.class);
		suite.addTestSuite(TabItemLocatorTest.class);
		suite.addTestSuite(TableCellLocatorTest.class);
		suite.addTestSuite(TableCellParentIndexTest.class);
		suite.addTestSuite(TableItemlLocatorTest.class);
		suite.addTestSuite(TextEntryTest.class);
		suite.addTestSuite(ToolItemInCoolBarLocatorTest.class);
		suite.addTestSuite(ToolItemLocatorTest.class);
		suite.addTestSuite(ToolItemLocatorTest2.class);
		suite.addTestSuite(TreeCellLocatorTest.class);
		suite.addTestSuite(TreeItemLocatorTest.class);

		suite.addTestSuite(ContainedInLocatorTest.class);
		suite.addTestSuite(ShellCloseTest.class);
		suite.addTestSuite(SliderLocatorTest.class);
		suite.addTestSuite(TreeItemLocatorEscapedDoubleSlashTest.class);
		suite.addTestSuite(TreeItemLocatorEscapedSlashTest.class);
		suite.addTestSuite(TreeItemLocatorPerforceTeamTagsTest.class);
		suite.addTestSuite(TreeItemLocatorSlashTest.class);
		//$JUnit-END$
		return suite;
	}

}
