package com.windowtester.test.gef.tests.legacy;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite for basic reference GEF tests.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class AllBasicGEFDrivingTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Basic GEF Driving tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ShapeDrivingTest.class);
		suite.addTestSuite(LogicDrivingTest.class);
		//$JUnit-END$
		return suite;
	}

}
