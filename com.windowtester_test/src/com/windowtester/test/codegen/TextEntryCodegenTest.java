package com.windowtester.test.codegen;

import junit.framework.TestCase;

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.generator.NewAPICodeBlockBuilder;
import com.windowtester.codegen.swt.SWTV2TestCaseBuilder;

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
public class TextEntryCodegenTest extends TestCase {

	private NewAPICodeBlockBuilder _builder;
	private SWTV2TestCaseBuilder _testBuilder;
	

	public void testEscapes() {
		String result =  buildEntry("C:\\a");
		assertEquals("ui.enterText(\"C:\\\\a\");", result);
	}
	
	public void testEscapesTrailing() {
		String result = buildEntry("C:\\");
		assertEquals("ui.enterText(\"C:\\\\\");", result);
	}
	
	public void testMultipleEscapes() {
		String result = buildEntry("C:\\Program Files\\tmp");
		assertEquals("ui.enterText(\"C:\\\\Program Files\\\\tmp\");", result);
	}

	public void testMultipleEscapesTrailing() {
		String result = buildEntry("C:\\Program Files\\tmp\\");
		assertEquals("ui.enterText(\"C:\\\\Program Files\\\\tmp\\\\\");", result);
	}
	
	public void testSingleEscape() {
		String result = buildEntry("\\");
		assertEquals("ui.enterText(\"\\\\\");", result);
	}

	public void testEscapedQuotes() {
		String result = buildEntry("say \"hello!\"");
		assertEquals("ui.enterText(\"say \\\"hello!\\\"\");", result);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Helpers
	//
	////////////////////////////////////////////////////////////////////////////////////////

	private String buildEntry(String text) {
		NewAPICodeBlockBuilder builder = getBuilder();
		CodeBlock block = builder.buildTextEntry(text);
		String result = block.toString().trim(); //remove trailing WS
		return result;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////////////////
	
	private NewAPICodeBlockBuilder getBuilder() {
		if (_builder == null) {
			_builder = new NewAPICodeBlockBuilder(getTestCaseBuilder());
		}
		return _builder;
	}

	private SWTV2TestCaseBuilder getTestCaseBuilder() {
		if (_testBuilder == null)
			_testBuilder = new SWTV2TestCaseBuilder("MockTest", "mock", null, null);
		return _testBuilder;
	}
	
}
