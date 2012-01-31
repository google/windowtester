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
package com.windowtester.internal.runtime.test;

import junit.framework.TestCase;

import com.windowtester.internal.runtime.junit.core.ITestIdentifier;
import com.windowtester.runtime.util.TestMonitor;

/**
 * An id for a JUnit3 test.
 */
public class JUnit3TestId implements ITestIdentifier {

	
	private final TestCase _testcase;

	public JUnit3TestId(TestCase testcase) {
		_testcase = testcase;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.util.ITestIdentifier#getName()
	 */
	public String getName() {
		if (_testcase == null)
			return TestId.unknown().getName();
		return getTestCaseID(_testcase);
	}

	
    private String getTestCaseID(TestCase testcase) {
        String name = testcase.getName();
        
        // TODO [author=Dan] Hack for JUnit 4... 
        // better way would be to have a JUnit4TestId class.
        
        if (name == null) {
			name = testcase.getClass().getName();
			name = name.substring(name.lastIndexOf('.') + 1);
		}
        
		return TestMonitor.getId(testcase.getClass(), name);
    }
	
}
