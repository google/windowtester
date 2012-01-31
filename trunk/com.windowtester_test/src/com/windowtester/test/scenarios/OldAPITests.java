package com.windowtester.test.scenarios;

//import com.windowtester.test.screencapture.OldAPIScreenCaptureTest;
//import com.windowtester.test.screencapture.OldUIContextAdapterTest;

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
public class OldAPITests {

	
	//REMOVED: https://fogbugz.instantiations.com/default.php?44001
	public static Test suite() {
		TestSuite suite = new PlatformTestSuite("Old API Tests");
//		suite.addTestSuite(OldAPIScreenCaptureTest.class);
//		suite.addTestSuite(OldUIContextAdapterTest.class);
		return suite;
	}

	
}
