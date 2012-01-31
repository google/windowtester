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


import com.windowtester.eclipse.ui.convert.rule.WTExpandImportsRule;

/**
 * Test the expansion of imports
 * 
 * @author Dan Rubel
 */
public class WTExpandImportsRuleTest extends AbstractWTConvertAPITest
{
	public void testExpandImports_1() throws IOException {
		testExpandImports("NewContactEmbeddedSwingTest.txt");
	}

	public void testExpandImports_2() throws IOException {
		testExpandImports("NewContactSwingTest.txt");
	}

	private void testExpandImports(String fileName) throws IOException {
		testRules("expandedimports", fileName, new WTConvertAPIRule[]{
			new WTExpandImportsRule("junit.extensions")
		});
	}
}
