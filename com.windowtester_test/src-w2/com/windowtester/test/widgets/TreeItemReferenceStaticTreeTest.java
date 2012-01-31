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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.internal.debug.ThreadUtil;
import com.windowtester.runtime.ClickDescription;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.drivers.TreeDriver;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;
import com.windowtester.test.locator.swt.AbstractLocatorTest;

public class TreeItemReferenceStaticTreeTest extends AbstractLocatorTest {

	
		public class ItemSelectedCondition implements ICondition {

			private final TreeItem item;

			ItemSelectedCondition(TreeItem item){
				this.item = item;
			}
			
			public boolean test() {
				return item == lastSelected;
			}
					
			@Override
			public String toString() {
				return "Item: " + item + " to be selected";
			}
			
		}
	
	
		protected Tree tree;
		private Shell shell;

		protected TreeItem lastSelected;
		
		@Override
		public void uiSetup() {
			ThreadUtil.startPrintStackTraces(5000);
			
			shell = new Shell (Display.getDefault());
			shell.setLayout(new FillLayout());
			tree = new Tree (shell, getTreeStyle());
						
			for (int i=0; i<4; i++) {
				TreeItem iItem = new TreeItem (tree, 0);
				iItem.setText ("TreeItem (0) -" + i);
				itemCreated(iItem);

				for (int j=0; j<4; j++) {
					TreeItem jItem = new TreeItem (iItem, 0);
					jItem.setText ("TreeItem (1) -" + j);
					itemCreated(jItem);
					for (int k=0; k<4; k++) {
						TreeItem kItem = new TreeItem (jItem, 0);
						kItem.setText ("TreeItem (2) -" + k);
						itemCreated(kItem);
						for (int l=0; l<4; l++) {
							TreeItem lItem = new TreeItem (kItem, 0);
							lItem.setText ("TreeItem (3) -" + l);
							itemCreated(lItem);
						}
					}
				}
			}
			
			tree.addListener (SWT.Selection, new Listener () {
				public void handleEvent (Event event) {
					String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
					System.out.println (event.item + " " + string);
					lastSelected = (TreeItem) event.item;
				}
			});
			
			shell.setSize (200, 200);
			shell.open ();
		}



		//intended to be overridden
		protected int getTreeStyle() {
			return SWT.BORDER;
		}
	


		protected void itemCreated(TreeItem iItem) {
			// hook for subclass contribs
		}


		@Override
		public void uiTearDown() {
			shell.dispose();
			//ThreadUtil.stopPrintStackTraces();
		}
		
		public void testGetItem() throws Exception {
			TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0");
			assertNotNull(item);
		}
	
		public void testExpandItemLevel1() throws Exception {
			TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0");
			item.expand();
			TreeItemReference[] items = item.getItems();
			for (TreeItemReference child : items)
				assertTrue(child.isVisible());
			getUI().assertThat(item.isExpanded());
		}
	
		public void testExpandItemLevel2() throws Exception {
			TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0/TreeItem (1) -0");
			item.expand();
			TreeItemReference[] items = item.getItems();
			for (TreeItemReference child : items)
				assertTrue(child.isVisible());
			getUI().assertThat(item.isExpanded());
		}
		
		
		public void testReveal() throws Exception {
			TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -0/TreeItem (3) -0");
			assertTrue(item.isVisible());
		}
		
		public void testClickLevel0() throws Exception {
			TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0");
			item.click(ClickDescription.forClick(1));
			getUI().assertThat(new ItemSelectedCondition(item.getWidget()));
		}
		
		public void testClickLevel1() throws Exception {
			TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0/TreeItem (1) -0");
			item.click(ClickDescription.forClick(1));
			getUI().assertThat(new ItemSelectedCondition(item.getWidget()));
		}
		
		public void testClickLevel2() throws Exception {
			TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "TreeItem (0) -0/TreeItem (1) -0/TreeItem (2) -0");
			item.click(ClickDescription.forClick(1));
			getUI().assertThat(new ItemSelectedCondition(item.getWidget()));
		}
		
		
		
}
