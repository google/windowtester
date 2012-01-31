package com.windowtester.test.codegen;

import junit.framework.TestCase;

import com.windowtester.codegen.SourceStringBuilder;
import com.windowtester.codegen.assembly.unit.ImportUnit;

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
public class SourceStringBuilderTest extends TestCase {

	
	private final SourceStringBuilder builder = new SourceStringBuilder(null);
	private final StringBuffer sb = new StringBuffer();
	
	public void testImport() {
		builder.addImport(sb, ImportUnit.forName("Foo"));
		assertEquals("import Foo;", sb.toString());
	}

	public void testStaticImport() {
		builder.addImport(sb, ImportUnit.forStatic(("com.acme.Foo.bar")));
		assertEquals("import static com.acme.Foo.bar;", sb.toString());
	}
	

	
}
