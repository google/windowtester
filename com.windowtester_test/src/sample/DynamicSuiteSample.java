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
package sample;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Phil Quitslund
 *
 */
public class DynamicSuiteSample {

	public static Test suite() {
		TestSuite suite = new TestSuite("sample.dynamic.suite");
		for (String string : getStringsToTest())
			suite.addTest(new StringTest(string));
		return suite;
	}
	
	
	public static class StringTest extends TestCase {
		private final String toTest;
		public StringTest(String toTest) {
			super("testNotNull");
			this.toTest = toTest;
		}
		
		public void testNotNull() throws Exception {
			assertNotNull(toTest);
		}		
	}
	
	public static String[] getStringsToTest() {
		//this would actually be dynamically generated
		return new String[]{"one", "two", null /* bang! */, "three"};
	}
}
