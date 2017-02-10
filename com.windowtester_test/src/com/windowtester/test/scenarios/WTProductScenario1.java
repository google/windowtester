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
package com.windowtester.test.scenarios;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IConditionMonitor;
import com.windowtester.runtime.condition.IHandler;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.test.eclipse.ImportExampleProjectTest;
import com.windowtester.test.eclipse.NewSimpleProjectTest;
import com.windowtester.test.eclipse.locator.ContributedToolItemLocatorSmokeTest;
import com.windowtester.test.prefpage.WindowTesterPrefPageTest;
import com.windowtester.test.product.docs.ValidateIntroPageContentTest;

public class WTProductScenario1
{
	public static Test suite() {
		TestSuite suite = new PlatformTestSuite("WTProductScenario1");

		addGlobalHandlers();
		
		suite.addTestSuite(NewSimpleProjectTest.class);
		//todo[pq]: reenable (https://fogbugz.instantiations.com/default.php?43778)
		//suite.addTestSuite(ShellMonitorSmokeTest.class);
		suite.addTestSuite(ContributedToolItemLocatorSmokeTest.class);
		suite.addTestSuite(WindowTesterPrefPageTest.class);
		//CodeCoverage plug-in is not included anymore
//		suite.addTestSuite(CodeCoveragePrefPageTest.class);
//		suite.addTestSuite(CodeCoverageViewTest.class);
		//EvalRegistration is not used anymore, because windowtester is open source now!
//		suite.addTestSuite(WindowTesterEvalRegTest.class);
		suite.addTestSuite(ImportExampleProjectTest.class);

		//disabled because testNonExistentMenuItem hangs for 3 hours ?!
//		suite.addTestSuite(NewAPIScreenCaptureTest.class);
		
		//REMOVED: https://fogbugz.instantiations.com/default.php?44001
		//suite.addTestSuite(NewUIContextAdapterTest.class);
		//suite.addTest(OldAPITests.suite());
		
		//functionality unimplemented
		//suite.addTestSuite(SwitchAndCloseWindowTest.class);
		
		suite.addTestSuite(ValidateIntroPageContentTest.class);

		return suite;
	}

	private static void addGlobalHandlers() {
		IConditionMonitor cm = ConditionMonitor.getInstance();
		final String title = "Incorrect version of product";
		cm.add(new ShellShowingCondition(title), new IHandler() {
			public void handle(IUIContext ui) throws Exception {
				ScreenCapture.createScreenCapture(title);
				ui.click(new ButtonLocator("Details >>"));
				ScreenCapture.createScreenCapture(title);
				Assert.fail(title);
			}
		});
	}

}
