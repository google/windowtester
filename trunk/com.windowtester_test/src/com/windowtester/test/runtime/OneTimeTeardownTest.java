package com.windowtester.test.runtime;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.windowtester.runtime.swt.UITestCaseSWT;

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
public class OneTimeTeardownTest {
	
	public static class TestA extends UITestCaseSWT {
		
		public static int teardownCount = 0;
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.common.UITestCaseCommon#oneTimeTearDown()
		 */
		@Override
		protected void oneTimeTearDown() throws Exception {
			System.out.println("TestA.oneTimeTearDown()");
			++teardownCount;
		}
		public void testSomething() throws Exception {}
	}
	
	public static class TestB extends UITestCaseSWT {
		
		public static int teardownCount = 0;
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.common.UITestCaseCommon#oneTimeTearDown()
		 */
		@Override
		protected void oneTimeTearDown() throws Exception {
			System.out.println("TestB.oneTimeTearDown()");
			++teardownCount;
		}
		public void testSomething() throws Exception {}
	}
	
	public static class TestC extends UITestCaseSWT {
		
		public static int teardownCount = 0;
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.common.UITestCaseCommon#oneTimeTearDown()
		 */
		@Override
		protected void oneTimeTearDown() throws Exception {
			System.out.println("TestC.oneTimeTearDown()");
			++teardownCount;
		}
		public void testSomething() throws Exception {}
	}
	
	public static class AssertCounts extends UITestCaseSWT {
		public void testVerifyTeardownA() throws Exception {
			assertEquals(1, TestA.teardownCount);
		}
		public void testVerifyTeardownB() throws Exception {
			assertEquals(1, TestB.teardownCount);
		}
		public void testVerifyTeardownC() throws Exception {
			assertEquals(1, TestC.teardownCount);
		}

	}	

	
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TestA.class);
		suite.addTestSuite(TestB.class);
		suite.addTestSuite(TestC.class);
		suite.addTestSuite(TestB.class);
		suite.addTestSuite(TestC.class);
		suite.addTestSuite(AssertCounts.class);
		return suite;
	}
	
	
	
	
	
	
}
