package com.windowtester.test.scenarios;

import junit.framework.Test;

import com.windowtester.test.eclipse.AllEnsureThatTests;
import com.windowtester.test.eclipse.CloseWelcomeTest;
import com.windowtester.test.eclipse.CompoundKeystrokeSmokeTest;
import com.windowtester.test.eclipse.LegacyExceptionTest;
import com.windowtester.test.locator.swt.AllLocatorTests;
import com.windowtester.test.locator.swt.forms.AllFormsUITests;
import com.windowtester.test.runtime.CloseNestedShellsTest;
import com.windowtester.test.runtime.KeyStrokeDecodingSmokeTest;
import com.windowtester.test.runtime.LifecycleTests;
import com.windowtester.test.swt.JFaceFlagResetTest;
import com.windowtester.test.util.junit.ManagedSuite;

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
public class WTRuntimeScenario2 {

	public static Test suite() {
		
		ManagedSuite suite = new ManagedSuite("WTRuntimeScenario2");

		suite.addTestSuite(CloseWelcomeTest.class);
		
		//SWT locator tests
		suite.addTest(AllLocatorTests.suite());

		//Forms
		suite.addTest(AllFormsUITests.suite());

		//JFace flag setting test
		suite.addTest(JFaceFlagResetTest.suite());

		//key strokes
		suite.addTestSuite(KeyStrokeDecodingSmokeTest.class);
		suite.addTestSuite(CompoundKeystrokeSmokeTest.class);

		//runtime tests
		suite.addTestSuite(LegacyExceptionTest.class);
		suite.addTestSuite(CloseNestedShellsTest.class);
		
        //lifecycle
		suite.addTest(LifecycleTests.suite());
		
		suite.addTest(AllEnsureThatTests.suite());
		
		return suite;
	}

}
