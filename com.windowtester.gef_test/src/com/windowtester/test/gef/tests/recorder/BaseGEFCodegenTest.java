package com.windowtester.test.gef.tests.recorder;

import junit.framework.TestCase;

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.generator.ICodeBlockBuilder;
import com.windowtester.codegen.generator.LocatorJavaStringFactory;
import com.windowtester.codegen.generator.PluggableCodeGenerator;
import com.windowtester.runtime.locator.ILocator;

/**
 * Base class for non-PDE GEF codegen verification.
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class BaseGEFCodegenTest extends TestCase {

	private final GEFTestCodeGenerator cg = new GEFTestCodeGenerator();
	
	@Override
	protected void setUp() throws Exception {
		//this is a little uncomfortable but we need to fake a contribution
		//via the plugin registry (but would like to do it w/o the 
		//Platform running
		LocatorJavaStringFactory.TestOverride.setToStringDelegate(cg);
	}
		
	protected PluggableCodeGenerator codegenerator() {
		return cg.getCodeGenerator();
	}
	
	protected ICodeBlockBuilder blockBuilder() {
		return codegenerator().getBlockBuilder();
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	//
	// Helpers
	//
	/////////////////////////////////////////////////////////////////////////////////
		
	protected String toJava(ILocator locator) {
		return cg.toJava(locator);
	}
	
	protected void assertEquals(String expected, CodeBlock block) {
		assertEquals(expected, block.toString().trim());
	}
}
