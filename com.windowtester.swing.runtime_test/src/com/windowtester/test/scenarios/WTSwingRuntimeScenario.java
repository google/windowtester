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
package com.windowtester.test.scenarios;

import junit.framework.Test;
import junit.framework.TestSuite;
import swing.samples.TreeDnDTest;
import test.locators.SwingWidgetLocatorsSerailizationTest;
import w2.testcases.JButtonTest;
import w2.testcases.JComboBoxTest;
import w2.testcases.TextComponentTest;

import com.windowtester.test.swing.condition.IUIConditionSwingTest;

import context2.testcases.JListTest;
import context2.testcases.JTableTest;
import context2.testcases.JTableTest2;
import context2.testcases.JTreeTest;
import context2.testcases.ListDnDTest;
import context2.testcases.NamedListTest;
import context2.testcases.NamedTableTest;
import context2.testcases.NamedTreeTest;
import context2.testcases.NamedWidgetLocatorTest;
import context2.testcases.SwingMenuTest;
import context2.testcases.SwingTextTest;
import context2.testcases.SwingTreeTest;
import context2.testcases.SwingTreeTest2;


public class WTSwingRuntimeScenario
{
	public static Test suite() {
		TestSuite suite = new TestSuite("WTSwingRuntimeScenario");

		suite.addTestSuite(JComboBoxTest.class);
		suite.addTestSuite(JButtonTest.class);
		suite.addTestSuite(JListTest.class);
		suite.addTestSuite(NamedListTest.class);
		suite.addTestSuite(JTableTest.class);
		suite.addTestSuite(JTableTest2.class);
		suite.addTestSuite(NamedTableTest.class);
		suite.addTestSuite(JTreeTest.class);
		suite.addTestSuite(SwingTreeTest.class);
		suite.addTestSuite(SwingTreeTest2.class);
		suite.addTestSuite(NamedTreeTest.class);
		suite.addTestSuite(SwingMenuTest.class);
		suite.addTestSuite(SwingTextTest.class);
		suite.addTestSuite(TextComponentTest.class);
		suite.addTestSuite(NamedWidgetLocatorTest.class);
		suite.addTestSuite(IUIConditionSwingTest.class);
	//	suite.addTestSuite(SWTSwingApplTest.class);
	//	suite.addTestSuite(JTableDnDTest.class);
		suite.addTestSuite(ListDnDTest.class);
		suite.addTestSuite(TreeDnDTest.class);
		
		suite.addTestSuite(SwingWidgetLocatorsSerailizationTest.class);

		return suite;
	}

}
