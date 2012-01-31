package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.test.locator.swt.shells.CheckFileTree;

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
public class TreeItemLocatorCheckboxViewerDiagnosticTest extends AbstractLocatorTest { 
	
	CheckFileTree tree;
	
	@Override
	public void uiSetup() {
		super.uiSetup();
		tree = new CheckFileTree();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tree.run();				
			}
		});
	}

	@Override
	public void uiTearDown() {
		tree.close();
		super.uiTearDown();
	}

	public void testCheckEvents_MANUAL() throws Exception {
		IUIContext ui = getUI();
		ui.click(new TreeItemLocator(WT.CHECK, "C:\\"));
		
		ui.wait(TimeElapsedCondition.minutes(1));
		
	}
	
	
}
