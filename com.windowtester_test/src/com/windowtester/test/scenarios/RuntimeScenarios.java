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

import com.windowtester.test.util.junit.ManagedSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RuntimeScenarios {

	public static Test suite() {
		TestSuite suite = new ManagedSuite(
				"Runtime scenarios");
		//$JUnit-BEGIN$
		suite.addTest(WTRuntimeScenario1.suite());
		suite.addTest(WTRuntimeScenario2.suite());
		suite.addTest(WTProductScenario1.suite());
		//$JUnit-END$
		return suite;
	}

}
