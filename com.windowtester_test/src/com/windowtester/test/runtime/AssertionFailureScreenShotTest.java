package com.windowtester.test.runtime;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.internal.debug.ThreadUtil;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.finder.RetrySupport;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.test.screencapture.ScreenCaptureManager;

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
public class AssertionFailureScreenShotTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("AssertionFailureScreenShotTest");
		suite.addTestSuite(ScreenShotTrigger.class);
		suite.addTestSuite(ScreenShotVerifierTest.class);		
		return suite;
	}
	
	private static final String EXPECTED = "Expected to fail --- ignore!";
	private static int screenShotCount;

	
	public static class ScreenShotTrigger extends UITestCaseSWT {

		@Override
		protected void setUp() throws Exception {
			super.setUp();
			ScreenCaptureManager.clearExistingScreenShotsForTest(this.getClass().getName());
			screenShotCount = ScreenCaptureManager.getScreenShotCount();
		}

		private MessageDialog dialog;

		public void testAssertionFailedWithInfoOpenCreatesScreenShot()
				throws Exception {
			
			final Shell activeShell = (Shell) RetrySupport.retryUntilResultIsNonNull(new RunnableWithResult() {
				public Object runWithResult() {
					return ShellFinder.getWorkbenchRoot();
				}
			});
			
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Shell shell = activeShell != null ? activeShell : new Shell(Display.getDefault());
			        dialog = new MessageDialog(shell, "Info", null, 
			                "Something", MessageDialog.INFORMATION,
			                new String[] { IDialogConstants.OK_LABEL }, 0);
			        dialog.open();					
				}
			});

			IUIContext ui = getUI();
			ui.wait(TimeElapsedCondition.milliseconds(250)); // brief pause to allow dialog to appear
			ui.wait(new ShellShowingCondition("Info"));
			fail(EXPECTED);
		}

		@Override
		protected void tearDown() throws Exception {
			if (dialog != null) {
				DisplayExec.sync(new Runnable(){
					public void run() {
						dialog.close();
					}
					
				});
				getUI().wait(new ShellDisposedCondition("Info"));
			}
				
			super.tearDown();
		}
		
		@Override
		public void runBare() throws Throwable {
			try {
				super.runBare();
			} catch (AssertionFailedError e) {
				String message = e.getMessage();
				if (message != null && message.equals(EXPECTED))
					return;
				System.err.println(">>>> testAssertionFailedWithInfoOpenCreatesScreenShot FAILED - thread dumps follow");
				ThreadUtil.printStackTraces();
				throw e;
			}
		}
	}
	
	
	public static class ScreenShotVerifierTest extends UITestCaseSWT {
		public void testScreenShotCountIncremented() {
//			System.out.println(new File(".").getAbsolutePath());
//			fail("");
			getUI().wait(new ICondition() {
				public boolean test() {
				
					int expectedCount = screenShotCount +1;
					int currentCount = ScreenCaptureManager.getScreenShotCount();
					//System.out.println(expectedCount + " == " + currentCount);
					return expectedCount == currentCount;
				}

				public String toString() {
					return "screenshot count to be incremented";
				}
			});

		}
	}
	
}
