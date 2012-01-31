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
package com.windowtester.test.locator.swt.forms;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllFormsUITests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.windowtester.test.locator.swt.forms");
		//$JUnit-BEGIN$
//		suite.addTestSuite(FormTextLocatorTest.class);
//		suite.addTestSuite(SectionFormTextLocatorTest.class);
//		suite.addTestSuite(HelpPageFormTextTest.class);
		suite.addTestSuite(HyperlinkLocatorTest.class);
		suite.addTestSuite(HelpPageHyperlinkTest.class);
			
		//$JUnit-END$
		return suite;
	}

}
