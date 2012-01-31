/**
 * 
 */
package com.windowtester.test.codegen;


import junit.framework.Test;
import junit.framework.TestSuite;

import com.windowtester.test.eclipse.codegen.ui.SetupHandlerProviderTest;
import com.windowtester.test.eclipse.codegen.ui.SetupHandlerTableStoreTest;

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
public class AllCodegenTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.windowtester.test.eclipse.codegen");
		//$JUnit-BEGIN$
		suite.addTestSuite(CustomBaseTestCodegenTest.class);
		suite.addTestSuite(DefaultBaseTestCodegenTest.class);
		suite.addTestSuite(SWTV2TestCaseBuilderTest.class);
		suite.addTestSuite(ClassNameTest.class);
		
		suite.addTestSuite(RCPSetupCodegenTest.class);
		suite.addTestSuite(SWTSetupCodegenTest.class);
		
		suite.addTestSuite(LocatorJavaStringFactoryTest.class);
		suite.addTestSuite(SWTAPICodeBlockBuilderTest.class);
		suite.addTestSuite(SourceStringBuilderTest.class);
		suite.addTestSuite(TextEntryCodegenTest.class);
		suite.addTestSuite(SetupHandlerProviderTest.class);
		
		suite.addTestSuite(SetupHandlerTableStoreTest.class);
		
		//$JUnit-END$
		return suite;
	}

}
