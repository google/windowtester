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
package com.windowtester.eclipse.ui.convert;

import java.io.IOException;


/**
 * Scan several java compilation units and compare the generated context
 * 
 * @author Dan Rubel
 */
public class WTConvertAPIContextBuilderTest extends AbstractWTConvertAPITest
{
	public void testBuildContext_1() throws Exception {
		WTConvertAPIContext result = buildContext("NewContactEmbeddedSwingTest.txt");
		assertTrue(result.getWTTypeNames().contains("junit.extensions.UITestCase"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.UITestCaseSWT"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.internal.swing.UIContextSwing"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.IUIContext"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swing.locator.LabeledTextLocator"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swt.condition.shell.ShellShowingCondition"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swt.locator.ButtonLocator"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swt.locator.MenuItemLocator"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swt.locator.TableItemLocator"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swt.locator.eclipse.ViewLocator"));
		assertEquals(11, result.getWTTypeNames().size());
	}
	
	public void testBuildContext_2() throws Exception {
		WTConvertAPIContext result = buildContext("NewContactSwingTest.txt");
		assertTrue(result.getWTTypeNames().contains("junit.extensions.ActivePDETestSuite"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.ActiveWorkbenchTestSuite"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.AssertUtils"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.EclipseLogUtil"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.ForkedPDETestCase"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.ForkedPDETestCase2"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.PDETestFixture"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.UIAssertionHelper"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.UITestCase"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.UITestCaseCommon"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.UITestCaseSWT"));
		assertTrue(result.getWTTypeNames().contains("junit.extensions.UITestCaseSwing"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.internal.swing.UIContextSwing"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.IUIContext"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swing.condition.WindowDisposedCondition"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swing.condition.WindowShowingCondition"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swing.locator.JButtonLocator"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swing.locator.LabeledTextLocator"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swt.locator.MenuItemLocator"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swt.locator.TableItemLocator"));
		assertTrue(result.getWTTypeNames().contains("com.windowtester.runtime.swt.locator.eclipse.ViewLocator"));
		assertEquals(21, result.getWTTypeNames().size());
	}
	
	/**
	 * Build the WT convertion context for the specified file
	 */
	private WTConvertAPIContext buildContext(String fileName) throws IOException {
		String source = getSource("original", fileName);
		WTConvertAPIContextBuilder fixture = new WTConvertAPIContextBuilder();
		WTConvertAPIContext result = fixture.buildContext(source);
//		System.out.println("======== " + fileName);
//		for (Iterator<String> iter = new TreeSet<String>(result.getWTTypeNames()).iterator(); iter.hasNext();)
//			System.out.println(iter.next());
		return result;
	}
}