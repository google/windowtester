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

public class RecorderScenarios {

	public static Test suite() {
		TestSuite suite = new ManagedSuite(
				"Recorder scenarios");
		//$JUnit-BEGIN$
		suite.addTest(WTRecorderScenario1.suite());
		suite.addTest(WTRecorderScenario2.suite());
		//$JUnit-END$
		return suite;
	}

}
