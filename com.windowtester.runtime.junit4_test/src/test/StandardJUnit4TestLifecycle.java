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
package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Phil Quitslund
 *
 */
public class StandardJUnit4TestLifecycle {
	
	@Before
	public void verifyBeforeTestName() {
		throw new RuntimeException(); //NOT THROWN?
		//assertEquals("test.LifecycleTest_verifyTestName", TestMonitor.getInstance().getCurrentTestCaseID());	
	}
	
	@After
	public void verifyAfterTestName() {
		//throw new RuntimeException();
		//assertEquals("test.LifecycleTest_verifyTestName", TestMonitor.getInstance().getCurrentTestCaseID());	
	}
	
	@Test
	public void someTest() {
		
	}
}
