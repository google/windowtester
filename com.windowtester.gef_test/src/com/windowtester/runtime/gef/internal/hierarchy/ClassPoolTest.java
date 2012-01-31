package com.windowtester.runtime.gef.internal.hierarchy;

import com.windowtester.internal.runtime.ClassReference;

import junit.framework.TestCase;

/**
 * ClassPool tests.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassPoolTest extends TestCase {

	/**
	 * Makes the classpool testable by making helper methods public.
	 * <p>
	 * Copyright (c) 2007, Instantiations, Inc.<br>
	 * All Rights Reserved
	 *
	 * @author Phil Quitslund
	 *
	 */
	class TestableClassPool extends ClassPool {
		@Override
		public void add(ClassReference ref) {
			super.add(ref);
		}
		@Override
		public boolean contains(String className) {
			return super.contains(className);
		}
	}
	
	
	public void testBasicGet() {
		ClassPool cp = new ClassPool();
		ClassReference ref = cp.get("java.lang.String");
		assertEquals("java.lang.String", ref.getName());
	}
	
	public void testGetCaches() {
		
		final int[] addCount = new int[1];
		ClassPool cp = new TestableClassPool() {
			@Override
			public void add(ClassReference ref) {
				++addCount[0];
				super.add(ref);
			}
		};
		
		cp.get("java.lang.String");
		assertEquals(1, addCount[0]);
		cp.get("java.lang.String");
		assertEquals(1, addCount[0]);
		
	}
	

	public void testAddInvariant() {
		ClassPool cp = new TestableClassPool();
		try {
			cp.add(null);
			fail("should test invariant");
		} catch (IllegalStateException e) {
			//pass
		}
	}
	
	public void testContainsInvariant() {
		ClassPool cp = new TestableClassPool();
		try {
			cp.contains(null);
			fail("should test invariant");
		} catch (IllegalStateException e) {
			//pass
		}
	}
	
}
