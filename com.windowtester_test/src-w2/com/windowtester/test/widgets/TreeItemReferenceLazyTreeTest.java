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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.internal.debug.ThreadUtil;
import com.windowtester.internal.runtime.condition.NotCondition;
import com.windowtester.runtime.swt.internal.drivers.TreeDriver;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;
import com.windowtester.test.locator.swt.AbstractLocatorTest;

public class TreeItemReferenceLazyTreeTest extends AbstractLocatorTest {

	
		protected Tree tree;
		private Shell shell;

		@Override
		public void uiSetup() {
			ThreadUtil.startPrintStackTraces(5000);
			
			final Display display = Display.getDefault();
			shell = new Shell (display);
			shell.setText ("Lazy Tree");
			shell.setLayout (new FillLayout ());
			tree = new Tree (shell, SWT.BORDER);
			for (int i=0; i<4; i++) {
				TreeItem item = new TreeItem (tree, 0);
				item.setText ("Item " + i);
				item.setData (i);
				new TreeItem (item, 0); //dummy
			}
			
			
			tree.addListener (SWT.Expand, new Listener () {
				public void handleEvent (final Event event) {
					expansionEvent(event);
					final TreeItem root = (TreeItem) event.item;
					TreeItem[] items = root.getItems();
					for (TreeItem child : items) {
						if (child.getData() == null)
							child.dispose();
					}
					TreeItem item = new TreeItem (root, 0);
					int num = ((Integer) root.getData()).intValue();
					num++;
					item.setText("Item " + num);
					item.setData(num);
					new TreeItem (item, 0); //dummy
				}

			});
			Point size = tree.computeSize (300, SWT.DEFAULT);
			int width = Math.max (300, size.x);
			int height = Math.max (300, size.y);
			shell.setSize (shell.computeSize (width, height));
			shell.open ();
//			while (!shell.isDisposed ()) {
//				if (!display.readAndDispatch ()) display.sleep ();
//			}
//			display.dispose ();

		}



		private void expansionEvent(Event event) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		@Override
		public void uiTearDown() {
			shell.dispose();
			//ThreadUtil.stopPrintStackTraces();
		}
		
		public void XtestDiagnstic() throws Exception {
			getUI().pause(6000);
		}
		
//		public void testGetItem() throws Exception {
//			TreeItemReference item = new TreeReference(tree).getItem("TreeItem (0) -0");
//			assertNotNull(item);
//		}
//	
		public void testExpandItem() throws Exception {
			TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "Item 0");
			item.expand();
			getUI().assertThat(item.isExpanded());
		}
		
	
		public void testReveal() throws Exception {
			TreeItemReference item = new TreeDriver().reveal(new TreeReference(tree), "Item 1/Item 2/Item 3");
			getUI().assertThat(new NotCondition(item.isExpanded()));
		}
		
		
}
