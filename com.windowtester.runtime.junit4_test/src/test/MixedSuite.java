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
package test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * JUnit, PDE-test: Runs on 3.4M5.
 * 
 * 
 *
 * @author Phil Quitslund
 *
 */
@RunWith(Suite.class)
@SuiteClasses({MixedSuite.JUnit4Test.class, MixedSuite.JUnit3Test.class, MixedSuite.JUnit3Suite.class})
public class MixedSuite {

	public static class JUnit3Test extends TestCase {
		public void testOne() {
			System.out.println("one");
		}
	}
	
	public static class JUnit4Test {
		public JUnit4Test() {}
		@Test
		public void verifyTwo() {
			System.out.println("two");
		}
	}
	
	public static class JUnit3Suite {
		public static junit.framework.Test suite() {
			TestSuite suite = new TestSuite();
			suite.addTestSuite(JUnit3Test.class);
			return suite;
		}
	}
	
	
}
