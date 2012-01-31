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
public class OneTimeSetupTest {
	
	public static class TestA extends UITestCaseSWT {
		
		public static int setupCount = 0;
		

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.common.UITestCaseCommon#oneTimeSetup()
		 */
		@Override
		protected void oneTimeSetup() throws Exception {
			System.out.println("TestA.oneTimeSetup()");
			++setupCount;
		}
		public void testSomething() throws Exception {}
	}
	
	public static class TestB extends UITestCaseSWT {
		
		public static int setupCount = 0;
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.common.UITestCaseCommon#oneTimeTearDown()
		 */
		@Override
		protected void oneTimeSetup() throws Exception {
			System.out.println("TestB.oneTimeSetup()");
			++setupCount;
		}
		public void testSomething() throws Exception {}
	}
	
	public static class TestC extends UITestCaseSWT {
		
		public static int setupCount = 0;
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.common.UITestCaseCommon#oneTimeTearDown()
		 */
		@Override
		protected void oneTimeSetup() throws Exception {
			System.out.println("TestC.oneTimeSetup()");
			++setupCount;
		}
		public void testSomething() throws Exception {}
	}
	
	public static class TestNotRun extends UITestCaseSWT {
		
		public static int setupCount = 0;
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.common.UITestCaseCommon#oneTimeTearDown()
		 */
		@Override
		protected void oneTimeSetup() throws Exception {
			System.out.println("TestNotRun.oneTimeSetup()");
			++setupCount;
		}
		public void testSomething() throws Exception {}
	}
	
	public static class AssertCounts extends UITestCaseSWT {
		public void testVerifySetupA() throws Exception {
			assertEquals(1, TestA.setupCount);
		}
		public void testVerifySetupB() throws Exception {
			assertEquals(1, TestB.setupCount);
		}
		public void testVerifySetupC() throws Exception {
			assertEquals(1, TestC.setupCount);
		}
		public void testVerifySetupNotRun() throws Exception {
			assertEquals(0, TestNotRun.setupCount);
		}
	}	

	
	
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TestA.class);
		suite.addTestSuite(TestB.class);
		suite.addTestSuite(TestC.class);
		suite.addTestSuite(TestA.class);
		suite.addTestSuite(TestA.class);
		suite.addTestSuite(TestC.class);
		suite.addTestSuite(AssertCounts.class);
		return suite;
	}
	
	
	
	
	
	
}
