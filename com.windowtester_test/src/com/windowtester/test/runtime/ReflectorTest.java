package com.windowtester.test.runtime;

import junit.framework.TestCase;

import com.windowtester.internal.runtime.reflect.Reflector;

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
public class ReflectorTest extends TestCase {

	
	public void testSupports() throws Exception {
		assertTrue(Reflector.forObject("Foo").supports("trim"));
	}
	
	public void testInvoke() throws Exception {
		assertEquals(3, Reflector.forObject("Foo").invoke("length"));
	}
		
	
}
