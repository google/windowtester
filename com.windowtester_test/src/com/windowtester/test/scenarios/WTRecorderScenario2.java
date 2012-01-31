package com.windowtester.test.scenarios;

import junit.framework.Test;

import com.windowtester.test.codegen.AllCodegenTests;
import com.windowtester.test.recorder.ui.AllEventSequenceTests;
import com.windowtester.test.recorder.ui.CommandStackTest;
import com.windowtester.test.recorder.ui.EventSequenceLabelProviderSmokeTest;
import com.windowtester.test.recorder.ui.EventSequenceModelTest;
import com.windowtester.test.recorder.ui.PropertyMappingTest;
import com.windowtester.test.recorder.ui.PropertySetTest;
import com.windowtester.test.recorder.ui.RecorderConsolePresenterTest;
import com.windowtester.test.recorder.ui.RecorderPanelModelTest;
import com.windowtester.test.recorder.ui.SessionMonitorTest;
import com.windowtester.test.recorder.ui.TargetFileParsingSmokeTest;
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
public class WTRecorderScenario2 {

	public static Test suite() {
		ManagedSuite suite = new ManagedSuite("WTRecorderScenario2");
		//$JUnit-BEGIN$
		suite.addTestSuite(CommandStackTest.class);
		suite.addTestSuite(RecorderPanelModelTest.class);
		suite.addTestSuite(SessionMonitorTest.class);
		suite.addTestSuite(RecorderConsolePresenterTest.class);
		suite.addTestSuite(EventSequenceModelTest.class);
		suite.addTestSuite(PropertyMappingTest.class);
		suite.addTestSuite(PropertySetTest.class);
		suite.addTestSuite(EventSequenceLabelProviderSmokeTest.class);
		suite.addTestSuite(TargetFileParsingSmokeTest.class);
		suite.addTest(AllCodegenTests.suite());
		suite.addTest(AllEventSequenceTests.suite());
		//$JUnit-END$
		return suite;
	}

}
