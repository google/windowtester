package com.windowtester.test.scenarios;

import junit.framework.Test;
import junit.framework.TestSuite;

public class GEFScenarios {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"GEFF Scenarios");
		//$JUnit-BEGIN$
		suite.addTest(WTGEFScenario1.suite());
		suite.addTest(WTGEFScenarioDemo.suite());
		//$JUnit-END$
		return suite;
	}

}
