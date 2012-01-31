package com.windowtester.test.scenarios;

import junit.framework.Test;

import com.windowtester.test.eclipse.ActiveEditorConditionSmokeTest;
import com.windowtester.test.eclipse.BEAContextMenuTest;
import com.windowtester.test.eclipse.CloseWelcomeTest;
import com.windowtester.test.eclipse.ConsoleViewFindingSmokeTest;
import com.windowtester.test.eclipse.CreateAndCloseSimpleFileTest;
import com.windowtester.test.eclipse.ErrorNotificationHandlingTest;
import com.windowtester.test.eclipse.ModifyCompilerSettingsTest;
import com.windowtester.test.eclipse.NativeDialogTest;
import com.windowtester.test.eclipse.ProjectExplorerStressTest;
import com.windowtester.test.eclipse.RecorderLaunchConfigTest;
import com.windowtester.test.eclipse.TextKeyStrokeSelectionTest;
import com.windowtester.test.eclipse.condition.AllEclipseConditionTests;
import com.windowtester.test.eclipse.locator.ActiveEditorLocatorSmokeTest;
import com.windowtester.test.eclipse.locator.DialogMessageLocatorSmokeTest;
import com.windowtester.test.eclipse.locator.EditorLocatorSmokeTest;
import com.windowtester.test.eclipse.locator.NavigatorDoubleClickTest;
import com.windowtester.test.eclipse.locator.ProblemViewTreeItemLocatorTest;
import com.windowtester.test.eclipse.locator.SectionLocatorSmokeTest;
import com.windowtester.test.eclipse.locator.ToolAndViewPullDownMenuSmokeTest;
import com.windowtester.test.eclipse.locator.ViewLocatorSmokeTest;
import com.windowtester.test.locator.swt.AllSpecialCaseTreeItemLocatorTests;
import com.windowtester.test.locator.swt.CTabItemCloseTest;
import com.windowtester.test.locator.swt.LabeledLabelLocatorTest;
import com.windowtester.test.locator.swt.MenuItemPathTest;
import com.windowtester.test.locator.swt.TreeCellLocatorPDETest;
import com.windowtester.test.prefpage.WTRuntimePreferenceSettingsSmokeTest;
import com.windowtester.test.runtime.AssertionFailureScreenShotTest;
import com.windowtester.test.runtime.ClassReferenceTest;
import com.windowtester.test.runtime.KeyStrokeDecodingSmokeTest;
import com.windowtester.test.runtime.PathStringTokenizerTest;
import com.windowtester.test.runtime.ReflectorTest;
import com.windowtester.test.runtime.TableDoubleClickTest;
import com.windowtester.test.runtime.TestMonitorTest;
import com.windowtester.test.runtime.TextUtilsTest;
import com.windowtester.test.swt.WidgetReferenceFindTest;
import com.windowtester.test.util.junit.ManagedSuite;

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
public class WTRuntimeScenario1 {

	public static Test suite() {
		ManagedSuite suite = new ManagedSuite("WTRuntimeScenario1");
		//$JUnit-BEGIN$
		suite.addTestSuite(CloseWelcomeTest.class);
		suite.addTestSuite(TestMonitorTest.class);
		suite.addTestSuite(ClassReferenceTest.class);
		suite.addTestSuite(KeyStrokeDecodingSmokeTest.class);
		suite.addTestSuite(ModifyCompilerSettingsTest.class);
	
		suite.addTestSuite(NativeDialogTest.class);
		
		suite.addTestSuite(ToolAndViewPullDownMenuSmokeTest.class);
		suite.addTestSuite(EditorLocatorSmokeTest.class);
		suite.addTestSuite(ActiveEditorLocatorSmokeTest.class);
		suite.addTestSuite(ActiveEditorConditionSmokeTest.class);
		suite.addTestSuite(ErrorNotificationHandlingTest.class);
		suite.addTestSuite(ConsoleViewFindingSmokeTest.class);
		suite.addTestSuite(RecorderLaunchConfigTest.class);
		suite.addTestSuite(TableDoubleClickTest.class);
		suite.addTestSuite(WTRuntimePreferenceSettingsSmokeTest.class);
		suite.addTestSuite(BEAContextMenuTest.class);
		suite.addTestSuite(WidgetReferenceFindTest.class);
		suite.addTestSuite(NavigatorDoubleClickTest.class);
		
		
		suite.addTest(AllEclipseConditionTests.suite());
		
		suite.addTestSuite(SectionLocatorSmokeTest.class);
		suite.addTestSuite(DialogMessageLocatorSmokeTest.class);
		suite.addTest(AssertionFailureScreenShotTest.suite());
		suite.addTestSuite(ProblemViewTreeItemLocatorTest.class);
		suite.addTestSuite(LabeledLabelLocatorTest.class);
		suite.addTestSuite(ViewLocatorSmokeTest.class);
		suite.addTest(AllSpecialCaseTreeItemLocatorTests.suite());
		suite.addTestSuite(PathStringTokenizerTest.class);
		suite.addTestSuite(TextUtilsTest.class);
		suite.addTestSuite(MenuItemPathTest.class);
		
		suite.addTestSuite(ReflectorTest.class);
		
		suite.addTestSuite(ProjectExplorerStressTest.class);
		suite.addTestSuite(CTabItemCloseTest.class);
		suite.addTestSuite(TreeCellLocatorPDETest.class);
		
		suite.addTestSuite(CreateAndCloseSimpleFileTest.class);
		
		//disabled --- very win32 specific and flaky --- solution likely pauses between keystrokes
		suite.addTestSuite(TextKeyStrokeSelectionTest.class);
		
		//disabled pending safer implementation of sendToBack
		//suite.addTest(WorkbenchFocusTests.suite());
		
		
		//$JUnit-END$
		return suite;
	}

}
