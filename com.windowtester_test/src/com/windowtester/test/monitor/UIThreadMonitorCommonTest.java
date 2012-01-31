package com.windowtester.test.monitor;

import junit.framework.TestCase;

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
public class UIThreadMonitorCommonTest extends TestCase
{
	public void testMonitor1() {
		int testDuration = 100; 
		int expectedDelay = 500;
		boolean isUIResponsive = true;
		Monitor monitor = new Monitor(testDuration, expectedDelay, isUIResponsive);
		assertEquals(false, monitor.wasTimeout());
		assertEquals(false, monitor.wasTimeoutResponsive());
	}

	public void testMonitor2() {
		long testDuration = 50000;
		int expectedDelay = 500;
		boolean isUIResponsive = false;
		Monitor monitor = new Monitor(testDuration, expectedDelay, isUIResponsive);
		assertEquals(true, monitor.wasTimeout());
		assertEquals(false, monitor.wasTimeoutResponsive());
	}

	public void testMonitor3() {
		long testDuration = 50000;
		long expectedDelay = 500;
		boolean isUIResponsive = true;
		Monitor monitor = new Monitor(testDuration, expectedDelay, isUIResponsive);
		assertEquals(true, monitor.wasTimeout());
		assertEquals(true, monitor.wasTimeoutResponsive());
	}
}