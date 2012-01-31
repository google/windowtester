package com.windowtester.test.eclipse.condition;

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
public class AllEclipseConditionTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.windowtester.test.eclipse.condition");
		//$JUnit-BEGIN$
		suite.addTestSuite(MenuItemSelectionTest.class);
		suite.addTestSuite(IUIConditionSWTTest.class);
		suite.addTestSuite(ViewShowingConditionHandlerTest.class);
		suite.addTestSuite(ViewShowingConditionSmokeTest.class);
		//todo[pq]: reenable (https://fogbugz.instantiations.com/default.php?43778)
		//suite.addTestSuite(ShellMonitorSmokeTest.class);
		//$JUnit-END$
		return suite;
	}

}
