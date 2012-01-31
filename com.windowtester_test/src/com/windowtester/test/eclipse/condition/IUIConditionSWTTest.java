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
package com.windowtester.test.eclipse.condition;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IConditionMonitor;
import com.windowtester.runtime.condition.IHandler;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.swt.UITestCaseSWT;


public class IUIConditionSWTTest extends UITestCaseSWT
{
	public void testIUIConditionInWait() {
		final boolean[] testCalled = new boolean[1];
		final boolean[] testUICalled = new boolean[1];
		IUICondition condition = new IUICondition() {
			public boolean test() {
				testCalled[0] = true;
				return true;
			}
			public boolean testUI(IUIContext ui) {
				testUICalled[0] = true;
				return true;
			}
		};
		
		// Assert that testUI is called and test is NOT called
		IUIContext ui = getUI();
		ui.wait(condition);
		assertTrue(testUICalled[0]);
		assertFalse(testCalled[0]);
	}
	
	public void testIUIConditionInMonitor() {
		final boolean[] testCalled = new boolean[1];
		final boolean[] testUICalled = new boolean[1];
		final boolean[] handlerCalled = new boolean[1];
		IUICondition condition = new IUICondition() {
			public boolean test() {
				testCalled[0] = true;
				return true;
			}
			public boolean testUI(IUIContext ui) {
				testUICalled[0] = true;
				return true;
			}
		};
		IHandler handler = new IHandler() {
			public void handle(IUIContext ui) throws Exception {
				handlerCalled[0] = true;
			}
		};
		
		// Assert that testUI is called and test is NOT called
		IUIContext ui = getUI();
		IConditionMonitor monitor = (IConditionMonitor) ui.getAdapter(IConditionMonitor.class);
		monitor.add(condition, handler);
		ui.handleConditions();
		assertTrue(testUICalled[0]);
		assertFalse(testCalled[0]);
		assertTrue(handlerCalled[0]);
	}
}
