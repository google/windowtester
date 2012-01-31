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
package com.windowtester.test.widgets;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllReferenceTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.windowtester.test.widgets");
		//$JUnit-BEGIN$
		suite.addTestSuite(CComboReferenceTest.class);
		suite.addTestSuite(ComboReferenceTest.class);
		suite.addTest(AllTreeTests.suite());
		//$JUnit-END$
		return suite;
	}

}
