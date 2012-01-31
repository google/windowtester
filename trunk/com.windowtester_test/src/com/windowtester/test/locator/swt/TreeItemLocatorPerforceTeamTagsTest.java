package com.windowtester.test.locator.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.TreeItemLocator;

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
public class TreeItemLocatorPerforceTeamTagsTest extends AbstractTreeItemLocatorTest {


	protected void createTreeContents(Tree tree) {
		treeOne(tree);
		//treeTwo(tree);
	}
	
	public void testDrive() throws Exception {
		IUIContext ui = getUI();
// (1)
		ui.click(new TreeItemLocator("( >)?LwsTest51297 \\[Pver: Sys\\]"));
		ui.click(new TreeItemLocator("LwsTest51297 [Pver: Sys]/(> )?PackB \\[BC : PackB \\/ 1.1.1;1\\]"));
		ui.click(new TreeItemLocator("LwsTest51297 [Pver: Sys]/PackB [BC : PackB \\/ 1.1.1;1]/PackB_FuncW [FC : PackB_FuncW \\/ 1.0.0;8]"));
		//Errors(.)*/Font Arial(size 12.0 pt) is not allowed."
//(2)		
//		ui.click(new TreeItemLocator("Errors(.*)/Font Arial(size 16.0 pt) is not allowed."));
		ui.pause(3000);
		
	}

	
	private void treeOne(Tree tree) {
		TreeItem root = new TreeItem(tree, SWT.NONE);
		root.setText("LwsTest51297 [Pver: Sys]");
		TreeItem child1 = new TreeItem(root, SWT.NONE);
		child1.setText("PackB [BC : PackB / 1.1.1;1]");
		//root.setExpanded(true);
		TreeItem child2 = new TreeItem(child1, SWT.NONE);
		child2.setText("PackB_FuncW [FC : PackB_FuncW / 1.0.0;8]");
		//child1.setExpanded(true);
	}

//	private void treeTwo(Tree tree) {
//		TreeItem root = new TreeItem(tree, SWT.NONE);
//		root.setText("Errors(2)");
//		TreeItem child1 = new TreeItem(root, SWT.NONE);
//		child1.setText("Font Arial(size 12.0 pt) is not allowed.");
//		root.setExpanded(true);
//		TreeItem child2 = new TreeItem(root, SWT.NONE);
//		child2.setText("Font Arial(size 16.0 pt) is not allowed.");
//		child1.setExpanded(true);
//		
//	}

	
	
	
	
}
