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
package com.windowtester.test.scenarios;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.windowtester.test.codegen.LocatorJavaStringFactoryTest;
import com.windowtester.test.codegen.SWTAPICodeBlockBuilderTest;
import com.windowtester.test.codegen.TextEntryCodegenTest;
import com.windowtester.test.eclipse.CloseWelcomeTest;
import com.windowtester.test.eclipse.codegen.BasicRecorderSmokeTests;
import com.windowtester.test.eclipse.codegen.BundleResolverSmokeTest;
import com.windowtester.test.eclipse.codegen.EclipseRecorderSmokeTests;
import com.windowtester.test.eclipse.codegen.LocatorBundleResolutionTest;
import com.windowtester.test.eclipse.identifier.AllIEclipseIdentifierTests;

public class WTRecorderScenario1
{
	public static Test suite() {
		TestSuite suite = new TestSuite("WTRecorderScenario1");

		suite.addTestSuite(CloseWelcomeTest.class);
		suite.addTestSuite(BasicRecorderSmokeTests.class);
		suite.addTestSuite(EclipseRecorderSmokeTests.class);
		suite.addTestSuite(SWTAPICodeBlockBuilderTest.class);
		suite.addTestSuite(LocatorJavaStringFactoryTest.class);
		suite.addTestSuite(BundleResolverSmokeTest.class);
		suite.addTestSuite(LocatorBundleResolutionTest.class);
		suite.addTestSuite(TextEntryCodegenTest.class);
		suite.addTest(AllIEclipseIdentifierTests.suite());

		return suite;
	}

}
