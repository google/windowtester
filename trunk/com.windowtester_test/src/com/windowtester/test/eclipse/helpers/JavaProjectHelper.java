package com.windowtester.test.eclipse.helpers;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.test.eclipse.EclipseUtil;
import com.windowtester.test.util.TypingLinuxHelper;

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
public class JavaProjectHelper extends TestHelper {

	
	private static final String SEP_SRC_FOLDER_CREATION_LABEL = EclipseUtil.isVersion_32() ? "&Create separate source and output folders" : "&Create separate folders for sources and class files";

	public static void createJavaProject(IUIContext ui, String projectName) throws WidgetSearchException {
		
		ensureNotNull(ui, projectName);
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();	
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("Next >"));
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), false));
//		new DebugHelper().printWidgets();
//		WidgetPrinter printer = new WidgetPrinter();
//		DisplayReference.getDefault().getActiveShell().accept(printer);
//		System.out.println(printer.asString());
//		Assert.fail();
		ui.click(new LabeledTextLocator("&Project name:"));
		ui.enterText(projectName);
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), true));
		ui.click(new ButtonLocator(SEP_SRC_FOLDER_CREATION_LABEL));
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		ui.wait(new JobsCompleteCondition());
		ui.wait(new SWTIdleCondition());
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}
	
	//NOTE: assumes target project is selected...
	public static void createJavaClass(IUIContext ui, String sourceFolder, String className) throws WidgetSearchException {
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();
		ensureNotNull(ui, className);		
		ui.click(new MenuItemLocator("File/New/Class"));
		ui.wait(new ShellShowingCondition("New Java Class"));
		ui.click(2, new LabeledTextLocator("Source fol&der:"));
		ui.enterText(sourceFolder);
		ui.click(2, new LabeledTextLocator("Na&me:"));
		ui.enterText(className);
		ui.assertThat(new ButtonLocator("Finish").isEnabled());
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Java Class"));
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}
	
	public static void createJavaClass(IUIContext ui, String sourceFolder, String packageName, String className) throws WidgetSearchException {
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();
		ensureNotNull(ui, className);		
		ui.click(new MenuItemLocator("File/New/Class"));
		ui.wait(new ShellShowingCondition("New Java Class"));
		ui.click(new LabeledTextLocator("Source fol&der:"));
		selectAll(ui);
		ui.enterText(sourceFolder);
		ui.click(new LabeledTextLocator("Pac&kage:"));
		ui.enterText(packageName);
		ui.click(new LabeledTextLocator("Na&me:"));
		selectAll(ui);
		ui.enterText(className);
		ui.assertThat(new ButtonLocator("Finish").isEnabled());
		ui.click(new ButtonLocator("Finish"));
		// extra long wait for shell disposed for Dan's slow Linux box
		ui.wait(new ShellDisposedCondition("New Java Class"));
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}
	
}
