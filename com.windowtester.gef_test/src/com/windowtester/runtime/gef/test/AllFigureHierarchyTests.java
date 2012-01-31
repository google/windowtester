package com.windowtester.runtime.gef.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.windowtester.runtime.gef.internal.ConnectionInfoTest;
import com.windowtester.runtime.gef.internal.FigureInfoStateTest;
import com.windowtester.runtime.gef.internal.FigureInfoTest;
import com.windowtester.runtime.gef.internal.hierarchy.ClassPoolTest;
import com.windowtester.runtime.gef.internal.hierarchy.FigureInfoBuilderTest;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class AllFigureHierarchyTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.windowtester.runtime.gef.internal.hierarchy");
		//$JUnit-BEGIN$
		suite.addTestSuite(FigureInfoBuilderTest.class);
		suite.addTestSuite(ClassPoolTest.class);
		suite.addTestSuite(FigureInfoTest.class);
		suite.addTestSuite(ConnectionInfoTest.class);
		suite.addTestSuite(FigureInfoStateTest.class);
		
		//$JUnit-END$
		return suite;
	}

}
