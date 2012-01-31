package com.windowtester.test.codegen;

import junit.framework.TestCase;

import com.windowtester.codegen.swt.ClassName;

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
public class ClassNameTest extends TestCase {

	
	public void testParseFQN() throws Exception {
		ClassName cls = ClassName.forQualifiedName("com.acme.Foo");
		assertEquals("Foo", cls.getClassName());
		assertEquals("com.acme", cls.getPackageName());
	}
	
	public void testParseFQN2() throws Exception {
		ClassName cls = ClassName.forQualifiedName("Foo");
		assertEquals("Foo", cls.getClassName());
		assertEquals("", cls.getPackageName());
	}
	
	
	public void testParseClass() throws Exception {
		ClassName cls = ClassName.forClass(Object.class);
		assertEquals("Object", cls.getClassName());
		assertEquals("java.lang", cls.getPackageName());
	}
	
}
