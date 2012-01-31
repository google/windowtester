package com.windowtester.test.util.junit;

import java.lang.reflect.Method;

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
public class ManagedSuite extends PluggableTestSuite {
	
	public ManagedSuite() {
		super();
	}
	
	public ManagedSuite(Class<?> testClass) {
		super(testClass);
	}
	
	public ManagedSuite(String name) {
		super(name);
	}

	@Override
	protected boolean isEnabled(Method m) {
		RunOn platform = m.getAnnotation(RunOn.class);
		if (undefined(platform))
			return true;
		return isCurrentPlatform(platform) && !excluded(platform);
	}

	private boolean isCurrentPlatform(RunOn runOn) {
		return runOn.value().isCurrent();
	}

	private boolean excluded(RunOn runOn) {
		OS[] excluded = runOn.but();
		for (OS os : excluded) {
			if (os.isCurrent())
				return true;
		}
		return false;
	}

	private boolean undefined(RunOn runOn) {
		return runOn == null;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	protected PluggableTestSuite newSuite(Class testClass) {
		return new ManagedSuite(testClass);
	}
} 

// Sample use:
//
//import junit.framework.Test;
//import junit.framework.TestCase;
//
//public class ManagedSuiteTest  {
//
//	public static class TestOS extends TestCase {
//		
//		@RunOn(OS.OSX)
//		public void testOSX() throws Exception {
//			System.out.println("ManagedSuiteTest.TestA.testOSX()");
//		}
//		
//		@RunOn(value=OS.ALL, but={OS.OSX})
//		public void testAllButOSX() throws Exception {
//			System.out.println("ManagedSuiteTest.TestA.testAllButOSX()");
//		}
//		
//		@RunOn(value=OS.ALL, but={OS.WIN})
//		public void testAllButWin() throws Exception {
//			System.out.println("ManagedSuiteTest.TestA.testAllButWin()");
//		}
//				
//		@RunOn(OS.WIN)
//		public void testWin() throws Exception {
//			System.out.println("ManagedSuiteTest.TestA.testWin()");
//		}
//		
//
//		@RunOn(OS.LINUX)
//		public void testLinux() throws Exception {
//			System.out.println("ManagedSuiteTest.TestA.testLinux()");
//		}
//
//		@RunOn(OS.ALL)
//		public void testAll() throws Exception {
//			System.out.println("ManagedSuiteTest.TestA.testAll()");
//		}
//
//		public void testDefault() throws Exception {
//			System.out.println("ManagedSuiteTest.TestA.testDefault()");
//		}
//	}
//	
//	public static Test suite() {
//		ManagedSuite suite = new ManagedSuite();
//		suite.addTestSuite(TestOS.class);
//		return suite;
//	}
//}
