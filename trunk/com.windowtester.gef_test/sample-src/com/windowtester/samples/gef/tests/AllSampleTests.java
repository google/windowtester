package com.windowtester.samples.gef.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Sample Test Suite.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 */
public class AllSampleTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.windowtester.samples.gef.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(SampleGEFLogicTest.class);
		suite.addTestSuite(SampleGEFShapeTest.class);
		//$JUnit-END$
		return suite;
	}

}
