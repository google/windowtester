package com.windowtester.test.runtime;

import com.windowtester.runtime.internal.TestClassManager;

import junit.framework.TestCase;

/**
 *
 * @author Phil Quitslund
 *
 */
public class TestClassManagerTest extends TestCase {

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
	private final class TestableManager extends TestClassManager {
		
		TestCase last;
		TestCase first;

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.internal.TestClassManager#firstRun(junit.framework.TestCase)
		 */
		@Override
		public void firstRun(TestCase t) {
			first = t;
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.internal.TestClassManager#lastRun(junit.framework.TestCase)
		 */
		@Override
		public void lastRun(TestCase t) {
			last = t;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.internal.TestClassManager#addTest(junit.framework.TestCase)
		 */
		@Override
		public TestableManager toRun(TestCase t) {
			// TODO Auto-generated method stub
			return (TestableManager) super.toRun(t);
		}
	}
	static class Test1 extends TestCase {}
	static class Test2 extends TestCase {}
	static class Test3 extends TestCase {}
	
	
	
	public void testAddTest() throws Exception {
		TestableManager manager = new TestableManager();
		
		TestCase t1 = new Test1();
		TestCase t2 = new Test2();
		TestCase t3 = new Test3();		
		manager.toRun(t1).toRun(t2).toRun(t3);
		
		assertEquals(t1, manager.tests().get(0));
		assertEquals(t2, manager.tests().get(1));
		assertEquals(t3, manager.tests().get(2));
	}
	
	public void testHasRun() throws Exception {
		TestableManager manager = new TestableManager();
		
		TestCase t1 = new Test1();
		manager.toRun(t1);
		assertFalse(manager.hasRun(t1));
		manager.runFinished(t1);
		assertTrue(manager.hasRun(t1));	
	}
	
	public void testClassHasRun() throws Exception {
		TestableManager manager = new TestableManager();
		
		TestCase t1 = new Test1();
		manager.toRun(t1);
		assertFalse(manager.hasClassRunCompleted(t1.getClass()));
		manager.runFinished(t1);
		assertTrue(manager.hasClassRunCompleted(t1.getClass()));	
	}
	
	public void testNoticeFirstRun() throws Exception {
		TestableManager manager = new TestableManager();
		TestCase t1 = new Test1();
		runTest(manager, t1);
		assertEquals(t1, manager.first);
	
	}

	private void runTest(TestClassManager manager, TestCase t3) throws Exception {
		manager.runStarted(t3);
		manager.runFinished(t3);
	}

	public void testNoticeLastRun() throws Exception {
		TestableManager manager = new TestableManager();
		TestCase t1 = new Test1();
		TestCase t2 = new Test2();
		TestCase t3 = new Test3();		
		manager.toRun(t1).toRun(t2).toRun(t3);
		
		runTest(manager, t1);
		manager.runStarted(t1);
		manager.runFinished(t2);
		manager.runStarted(t2);
		
		assertEquals(t2, manager.last);
	
	}
	
	
	public void testNoticeFirstAndLastRun() throws Exception {
		TestableManager manager = new TestableManager();
		TestCase t1 = new Test1();
		TestCase t2 = new Test2();
		TestCase t3 = new Test3();		
		manager.toRun(t1).toRun(t2).toRun(t3);
		
		runTest(manager, t1);
		assertEquals(t1, manager.first);
		assertEquals(t1, manager.last);
	}
	
	
	public void testNoticeFirstButNotLastRun() throws Exception {
		TestableManager manager = new TestableManager();
		TestCase t1 = new Test1();	
		manager.toRun(t1).toRun(t1);
		
		runTest(manager, t1);
		assertEquals(t1, manager.first);
		assertEquals(null, manager.last);
	}
	
	
}
