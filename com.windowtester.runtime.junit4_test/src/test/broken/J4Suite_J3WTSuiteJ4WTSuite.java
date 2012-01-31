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
package test.broken;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.JUnit3WTSuite;
import test.JUnit4J3WTSuite;


/**
 * A suite that mixes JUnit3 and JUnit4 suites.
 * 
 * @author Phil Quitslund
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( {
	/*
	 * One or the other works but not both!
	 */
	JUnit3WTSuite.class,
	JUnit4J3WTSuite.class

})
public class J4Suite_J3WTSuiteJ4WTSuite {

//	//Not working --- TODO: J4Suite{J3Suite}
//	public static class JUnit3Suite {
//		public static junit.framework.Test suite() {
//			TestSuite suite = new TestSuite();
//			suite.addTestSuite(OpenAndClosePreferencesJUnit3Test.class);
//			return suite;
//		}
//	}
	
	

}
