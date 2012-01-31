package com.windowtester.test.eclipse;

import java.io.File;


import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
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
public class ImportProjectTest extends UITestCaseSWT
{
	public void testImportContactManagerSwing() throws Exception {
		importProject("ContactManagerSwing");
	}

	private void importProject(String projectName) throws Exception {
		File rootDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		String projectPath = new File(rootDir, projectName).getPath();
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/Import..."));
		ui.wait(new ShellShowingCondition("Import"));

		// Eclipse 3.1 uses a table while Eclipse 3.2 and beyond use a tree
		if (EclipseUtil.isVersion_31())
			ui.click(new TableItemLocator("Existing Projects into Workspace"));
		else
			ui.click(new TreeItemLocator("General/Existing Projects into Workspace"));
		
		ui.click(new ButtonLocator("Next >"));
		ui.click(new SWTWidgetLocator(Text.class, 0, new SWTWidgetLocator(Composite.class)));
		ui.enterText(projectPath);
		ui.keyClick(WT.TAB);
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("Import"));
		ui.wait(new JobsCompleteCondition());
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}
}