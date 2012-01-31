package com.windowtester.test.gef.tests.runtime.reflect;

import com.windowtester.runtime.gef.internal.reflect.IdIntrospector;

import junit.framework.TestCase;

/**
 * Basic ID introspection test.
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class IdIntrospectorTest extends TestCase {

	
	public void testId() throws Exception {
		assertEquals("Foo", IdIntrospector.forName("toString").getId("Foo"));
	}
}
