package com.windowtester.test.scenarios;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.windowtester.test.gef.tests.smoke.scenarios.FlowDrivingSmokeTest1;
import com.windowtester.test.gef.tests.smoke.scenarios.LogicDrivingSmokeTest1;
import com.windowtester.test.gef.tests.smoke.scenarios.LogicDrivingSmokeTest2;
import com.windowtester.test.gef.tests.smoke.scenarios.ShapeDrivingSmokeTest1;
import com.windowtester.test.gef.tests.smoke.scenarios.TextDrivingSmokeTest1;

/**
 * GEF test scenario- DEMO.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 * @author Jaime Wren
 *
 */
public class WTGEFScenarioDemo {
	public static Test suite() {
		TestSuite suite = new TestSuite("WTGEFScenarioDemo");
		
		suite.addTestSuite(ShapeDrivingSmokeTest1.class);
		//suite.addTestSuite(ShapeDrivingSmokeTest2.class);
		
		suite.addTestSuite(LogicDrivingSmokeTest1.class);
		suite.addTestSuite(LogicDrivingSmokeTest2.class);
		
		suite.addTestSuite(FlowDrivingSmokeTest1.class);
		
		suite.addTestSuite(TextDrivingSmokeTest1.class);
		
		// ensure editors saved so scenario does not hang on shutdown
		// but all the tests save.. so this fails
		//suite.addTestSuite(SaveAllTest.class);
		
		return suite;
	}
}
