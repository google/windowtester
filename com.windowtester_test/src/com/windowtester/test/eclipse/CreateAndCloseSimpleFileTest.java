package com.windowtester.test.eclipse;

import static com.windowtester.test.eclipse.helpers.SimpleProjectHelper.createSimpleFile;
import static com.windowtester.test.eclipse.helpers.SimpleProjectHelper.createSimpleProject;

import org.eclipse.core.runtime.Path;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.locator.MenuItemLocator;

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
public class CreateAndCloseSimpleFileTest extends BaseTest {

	
	public void testCreateAndCloseSimpleFile() throws Exception {
		
		IUIContext ui = getUI();
		String projectName = getClass().getSimpleName() + "Project";
		
		createSimpleProject(ui, projectName);
		
		for (int index = 1; index < 5; ++index) {
			createSimpleFile(ui, new Path(projectName), "file" + index + ".txt");
			closeFile(ui);
		}
				
	}

	private void closeFile(IUIContext ui) throws WidgetSearchException {
		ui.click(new MenuItemLocator("File/Close"));
	}
	
}
