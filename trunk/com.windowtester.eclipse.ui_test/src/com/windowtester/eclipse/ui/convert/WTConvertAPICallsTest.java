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
package com.windowtester.eclipse.ui.convert;

import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.eclipse.ui.convert.rule.WTReplaceIUIContextMethodCallWithEnsureThatRule;
import com.windowtester.eclipse.ui.convert.rule.WTReplacePauseCallsRule;
import com.windowtester.eclipse.ui.convert.rule.WTReplaceSWTWidgetLocatorRule;
import com.windowtester.runtime.swt.locator.ShellLocator;

/**
 * Tests various API call conversions.
 * 
 * @author Phil Quitslund
 */
public class WTConvertAPICallsTest extends AbstractWTConvertAPITest
{
	public void testConvertPause_1() throws Exception {
		testConvertAPI("SleepTest1.txt");
	}

	public void testConvertPause_2() throws Exception {
		testConvertAPI("SleepTest2.txt");
	}

	public void testConvertPause_3() throws Exception {
		testConvertAPI("SleepTest3.txt");
	}

	public void testConvertPause_4() throws Exception {
		testConvertAPI("SleepTest4.txt");
	}

	public void testConvertClose() throws Exception {
		testConvertAPI("CloseTest.txt");
	}

	private void testConvertAPI(String fileName) throws IOException {
		testRules("apiCalls", fileName, new WTConvertAPIRule[]{
			new WTReplaceSWTWidgetLocatorRule(Shell.class, ShellLocator.class),
			new WTReplaceIUIContextMethodCallWithEnsureThatRule("setFocus", "hasFocus"),
			new WTReplaceIUIContextMethodCallWithEnsureThatRule("close", "isClosed"), 
			new WTReplacePauseCallsRule()
		});
	}
}
