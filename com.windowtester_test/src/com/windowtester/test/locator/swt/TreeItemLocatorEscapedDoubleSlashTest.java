package com.windowtester.test.locator.swt;

import static com.windowtester.runtime.swt.locator.SWTLocators.treeItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.runtime.IUIContext;

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
public class TreeItemLocatorEscapedDoubleSlashTest extends AbstractTreeItemLocatorTest {


	protected void createTreeContents(Tree tree) {
		TreeItem root = new TreeItem(tree, SWT.NONE);
		root.setText("Project\\\\Category1");
		TreeItem child1 = new TreeItem(root, SWT.NONE);
		child1.setText("child");
	}
	
	public void testFind() throws Exception {
		IUIContext ui = getUI();
		ui.find(treeItem("Project\\\\Category1"));
	}

	public void testClick() throws Exception {
		IUIContext ui = getUI();
		ui.click(treeItem("Project\\\\Category1"));
	}

	
	
	
	
}
