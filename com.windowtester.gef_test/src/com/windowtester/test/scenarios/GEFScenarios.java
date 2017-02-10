package com.windowtester.test.scenarios;

import junit.framework.Test;
import junit.framework.TestSuite;

public class GEFScenarios {

	public static Test suite() {
		TestSuite suite = new TestSuite("GEF Scenarios");
		//$JUnit-BEGIN$
		suite.addTest(WTGEFScenario1.suite());
//		suite.addTest(WTGEFScenarioDemo.suite()); //disabled because all tests are already contained in WTGEFScenario1
		//$JUnit-END$
		return suite;
	}

}