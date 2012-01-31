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
package com.windowtester.runtime.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * Manage test run lifecycle for classes (onetime calls).
 */
public class TestClassManager {

	private List toRun  = new ArrayList();
	private List hasRun = new ArrayList();

	public TestClassManager toRun(TestCase t) {
		toRun.add(t);
		return this;
	}

	public List tests() {
		return toRun;
	}

	
	//hooks
	public void firstRun(TestCase t) throws Exception {
		//default: no-op
	}
	
	public void lastRun(TestCase t) throws Exception {
		//default: no-op
	}


	public void runStarted(TestCase t) throws Exception {
		if (!testsRunContainTestWithClass(t.getClass()))
			firstRun(t);
	}

	public boolean hasRun(TestCase t) {
		return hasRun.contains(t);
	}

	public boolean hasClassRunCompleted(Class testClass) {
		return !testsToRunContainTestWithClass(testClass);
	}

	
	private boolean testsRunContainTestWithClass(Class testClass) {
		for (Iterator iter = hasRun.iterator(); iter.hasNext();) {
			TestCase next = (TestCase)iter.next();
			if (next == null)
				continue;
			if (next.getClass().equals(testClass))
				return true;
		}
		return false;
	}

	private boolean testsToRunContainTestWithClass(Class testClass) {
		for (Iterator iter = toRun.iterator(); iter.hasNext();) {
			TestCase next = (TestCase)iter.next();
			if (next == null)
				continue;
			if (next.getClass().equals(testClass))
				return true;
		}
		return false;
	}

	public void runFinished(TestCase t) throws Exception {
		removeTestFromToRunList(t);
		addTestToHasRunList(t);
		if (hasClassRunCompleted(t.getClass()))
			lastRun(t);
	}


	private void addTestToHasRunList(TestCase t) {
		hasRun.add(t);
	}

	protected void removeTestFromToRunList(TestCase t) {
		toRun.remove(t);
	}
		
}
