package com.windowtester.test.recorder.ui;

import junit.framework.Test;
import junit.framework.TestSuite;

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
public class AllEventSequenceTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Event sequence tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(EventSequenceOptimizerTest.class);
		suite.addTestSuite(EnteredKeyLabelProviderTest.class);
		suite.addTestSuite(EventSequenceModelTest.class);
		suite.addTestSuite(ParsedEventSequenceTest.class);
		suite.addTestSuite(EventSequenceLabelProviderSmokeTest.class);
		//$JUnit-END$
		return suite;
	}

}
