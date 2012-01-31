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
import com.windowtester.runtime.swt.internal.drivers.TreeDriver;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;
import com.windowtester.test.locator.swt.AbstractLocatorTest;

// TODO[pq]: this should get refreshed by another thread to be more true to life...
public class TreeItemReferenceVirtualTreeTest extends AbstractLocatorTest {

	
		protected Tree tree;
		private Shell shell;

		@Override
		public void uiSetup() {
			ThreadUtil.startPrintStackTraces(5000);
			
			final Display display = Display.getDefault();
			shell = new Shell (display);
			shell.setLayout (new FillLayout());
			tree = new Tree(shell, SWT.VIRTUAL | SWT.BORDER);
			tree.addListener(SWT.SetData, new Listener() {
				public void handleEvent(Event event) {
					TreeItem item = (TreeItem)event.item;
					TreeItem parentItem = item.getParentItem();
					String text = null;
					if (parentItem == null) {
						text = "Item " + tree.indexOf(item);
					} else {
						expansionEvent(event);
						text = "Item " +parentItem.indexOf(item);
					}
					item.setText(text);
					item.setItemCount(10);
				}
			});
			tree.setItemCount(20);
			shell.setSize(400, 300);
			shell.open();
//			while (!shell.isDisposed ()) {
//				if (!display.readAndDispatch ()) display.sleep ();
//			}
//			display.dispose ();


		}


		private void expansionEvent(Event event) {
//						System.out
//					.println("TreeItemReferenceVirtualTreeTest.expansionEvent()");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	//		expanding = false;
		}
		
		
		@Override
		public void uiTearDown() {
			shell.dispose();
			//ThreadUtil.stopPrintStackTraces();
		}
		
		public void XtestDiagnostic() throws Exception {
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
			assertTrue(item.isVisible());
		}
		
		
}
