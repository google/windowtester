package com.windowtester.test.eclipse;

import static com.windowtester.test.eclipse.helpers.SimpleProjectHelper.createSimpleFile;
import static com.windowtester.test.eclipse.helpers.SimpleProjectHelper.createSimpleProject;

import org.eclipse.core.runtime.Path;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.eclipse.ActiveEditorCondition;

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
public class ActiveEditorConditionSmokeTest extends BaseTest {

		public void testCondition() throws WidgetSearchException {
			IUIContext ui = getUI();
			String projectName = getClass().getSimpleName();
			String fileName = "file.txt";
			createSimpleProject(ui, projectName);
			createSimpleFile(ui, new Path(projectName), fileName);
			ui.wait(ActiveEditorCondition.forPath(new Path(projectName + "/" + fileName)), 5000);
		}
}
