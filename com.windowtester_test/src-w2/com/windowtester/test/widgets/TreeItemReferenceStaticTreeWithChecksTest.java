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
package com.windowtester.test.widgets;

import org.eclipse.swt.SWT;

import com.windowtester.runtime.swt.internal.drivers.TreeDriver;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;

public class TreeItemReferenceStaticTreeWithChecksTest extends TreeItemReferenceStaticTreeTest {


	@Override
	protected int getTreeStyle() {
		return super.getTreeStyle() | SWT.CHECK;
	}
		
	public void testCheckLevel0() throws Exception {
		TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0");
		item.check();
		getUI().assertThat(item.isChecked());
	}
	
	public void testCheckLevel1() throws Exception {
		TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0/TreeItem (1) -0");
		item.check();
		getUI().assertThat(item.isChecked());
	}

	public void testCheckLevel2() throws Exception {
		TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -0");
		item.check();
		getUI().assertThat(item.isChecked());
	}
}
