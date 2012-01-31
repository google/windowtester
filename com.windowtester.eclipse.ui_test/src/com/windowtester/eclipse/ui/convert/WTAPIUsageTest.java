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
public class WTAPIUsageTest extends AbstractWTConvertAPITest
{
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public void testBuildContext_1() throws Exception {
		apiUsage("NewContactEmbeddedSwingTest.txt");
	}

	public void testBuildContext_2() throws Exception {
		apiUsage("NewContactSwingTest.txt");
	}

	public void testBuildContext_3() throws Exception {
		apiUsage("NewEntryTest.txt");
	}

	public void testBuildContext_4() throws Exception {
		apiUsage("SampleApplication.txt");
	}

	private void apiUsage(String fileName) throws IOException {
		String source = getSource("original", fileName);
		WTAPIUsage fixture = new WTAPIUsage();
		fixture.scanCompilationUnitSource(source);
		String actualText = fixture.getAPIUsageText();

		String expectedText = getSource("apiUsage", fileName);

		// Normalize line ends
		actualText = actualText.replaceAll("\r\n", "\n");
		expectedText = expectedText.replaceAll("\r\n", "\n");
		
		assertEquals(expectedText, actualText);
	}
}