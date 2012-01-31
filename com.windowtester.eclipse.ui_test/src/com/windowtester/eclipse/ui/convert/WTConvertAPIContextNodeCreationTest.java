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

import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;

public class WTConvertAPIContextNodeCreationTest extends AbstractWTConvertAPIContextTest
{
	public void testNewSimpleName() throws Exception {
		String source = getSource("original", "NewContactSwingTest.txt");
		WTConvertAPIContext context = new WTConvertAPIContextBuilder().buildContext(source);
		SimpleName actual = context.newSimpleName("TestClass", 19);
		assertEquals(19, actual.getStartPosition());
		assertEquals(9, actual.getLength());
	}
	
	public void testNewName1() throws Exception {
		String source = getSource("original", "NewContactSwingTest.txt");
		WTConvertAPIContext context = new WTConvertAPIContextBuilder().buildContext(source);
		Name actual = context.newName("TestClass", 27);
		assertTrue(actual instanceof SimpleName);
		SimpleName name = (SimpleName) actual;
		assertEquals(27, name.getStartPosition());
		assertEquals(9, name.getLength());
	}
	
	public void testNewName2() throws Exception {
		String source = getSource("original", "NewContactSwingTest.txt");
		WTConvertAPIContext context = new WTConvertAPIContextBuilder().buildContext(source);
		Name actual = context.newName("fully.qualified.TestClass", 83);
		assertTrue(actual instanceof QualifiedName);
		QualifiedName name = (QualifiedName) actual;
		assertEquals(83, name.getStartPosition());
		assertEquals(25, name.getLength());
		Name qualifier = name.getQualifier();
		assertEquals(83, qualifier.getStartPosition());
		assertEquals(15, qualifier.getLength());
		assertEquals(99, name.getName().getStartPosition());
		assertEquals(9, name.getName().getLength());
	}
}
