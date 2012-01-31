package com.windowtester.test.eclipse.helpers;

import static junit.framework.Assert.assertFalse;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IsEnabledCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.eclipse.FileExistsCondition;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.eclipse.ProjectExistsCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
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
public class SimpleProjectHelper extends TestHelper {

	
	public static void createSimpleProject(IUIContext ui, String projectName) throws WidgetSearchException {
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();
		ensureNotNull(ui, projectName);		
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("(Simple|General)/Project"));
		ui.click(new ButtonLocator("Next >"));
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), false));
		ui.enterText(projectName);
		ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), true));
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Project"));
		ui.wait(new ProjectExistsCondition(projectName, true));
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}	
	}
	

	public static IPath createSimpleFile(IUIContext ui, IPath outputPath,
			String fileName) throws WidgetSearchException {
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();
		ensureNotNull(ui, outputPath, fileName);
		assertFalse(outputPath.isEmpty());

		IPath fullPath = null;

		ui.click(new MenuItemLocator("File/New/Other..."));
		ui.wait(new ShellShowingCondition("New"));
		
		String filePath = "(General|Simple)/File";
		IWidgetLocator fileLocator = EclipseUtil.isVersion_31() ? new TreeItemLocator(filePath) : new FilteredTreeItemLocator(filePath);
		ui.click(fileLocator);
		
		ui.click(new ButtonLocator("Next >"));
		selectAll(ui, new LabeledTextLocator("&Enter or select the parent folder:"));
		

		String path = getPathString(outputPath);
		ui.enterText(path);
		selectAll(ui, new LabeledTextLocator("File na&me:"));
		ui.enterText(fileName);
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New File"));

		fullPath = outputPath.append(new Path(fileName));
		ui.wait(new FileExistsCondition(fullPath, true));
		return fullPath;
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}

	
	public static IPath createFolder(IUIContext ui, IPath outputPath,
			String fileName) throws WidgetSearchException {
		try{
		ensureNotNull(ui, outputPath, fileName);
		assertFalse(outputPath.isEmpty());

		IPath fullPath = null;

		ui.click(new MenuItemLocator("File/New/Other..."));
		ui.wait(new ShellShowingCondition("New"));
		
		String filePath = "(General|Simple)/Folder";
		IWidgetLocator fileLocator = EclipseUtil.isVersion_31() ? new TreeItemLocator(filePath) : new FilteredTreeItemLocator(filePath);
		ui.click(fileLocator);
		
		ui.click(new ButtonLocator("Next >"));
		selectAll(ui, new LabeledTextLocator("&Enter or select the parent folder:"));
		

		String path = getPathString(outputPath);
		ui.enterText(path);
		selectAll(ui, new LabeledTextLocator("Folder name:"));
		ui.enterText(fileName);
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Folder"));

		fullPath = outputPath.append(new Path(fileName));
		ui.wait(new FileExistsCondition(fullPath, true));
		return fullPath;
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}
	

	public static void selectAll(IUIContext ui, IWidgetLocator textLocator) throws WidgetSearchException {
		//OS-specific hack? :: double click to select all
		//ui.click(2, textLocator);
		ui.click(textLocator);
		selectAll(ui);
		
	}

	/**
	 * Create a plug-in project with the given name.
	 * @param ui the UI context to use
	 * @param projectName the name of the project to be created
	 * @throws WidgetSearchException
	 */
	static public void createPluginProject(IUIContext ui, String projectName) throws WidgetSearchException {
		ensureNotNull(ui, projectName);
		try{
			TypingLinuxHelper.switchToInsertStrategyIfNeeded();
			ui.click(new MenuItemLocator("File/New/Project..."));
			ui.wait(new ShellShowingCondition("New Project"));
			ui.click(new TreeItemLocator("Plug-in Project"));
			ui.click(new ButtonLocator("Next >"));
			ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), false));
			ui.enterText(projectName);
			ui.click(new ButtonLocator("Next >"));
			ui.assertThat(new IsEnabledCondition(new ButtonLocator("Finish"), true));
			ui.click(new ButtonLocator("Finish"));
			ui.wait(new ShellDisposedCondition("New Project"));
			ui.wait(new JobsCompleteCondition());
			ui.wait(new SWTIdleCondition());
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}

}
