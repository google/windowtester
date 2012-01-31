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
package com.windowtester.eclipse.ui.inspector;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.windowtester.recorder.event.user.IWidgetDescription;

/**
 * Section describing Locator details.
 */
public class LocatorSection extends AbstractSection {

	public void addTo(IWidgetDescription description, final ScrolledForm form,
			InspectorFormToolkit toolkit) {
		
		final Section section = createSection(form, toolkit);
		section.setText(getExpandedTitle());
		
		final Tree tree = LocatorTree.forLocatorInComposite(description.getLocator(), section);
		section.setClient(tree);
		toolkit.adapt(tree);		
		
//		TableWrapData layoutData = setLayoutAdjustingForTreeHeight(section, tree);
		
		
		expandAll(tree);
//		tree.setLayoutData(layoutData);
		
		TableWrapData td = new TableWrapData();
		td.align = TableWrapData.FILL;
		td.grabHorizontal = true;
		tree.setLayoutData(td);
		section.setLayoutData(td);
		
		
		//tree.layout();
		
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				if (e.getState() == false) {
					TreeItem[] items = tree.getItems();
					if (items.length > 0) {
						section.setText(items[0].getText());
						section.layout();
					}
				} else {
					section.setText(getExpandedTitle());
					section.layout();
				}
				form.getShell().pack(true);
			}
		});
//		tree.addListener(SWT.Expand, new Listener() {
//			public void handleEvent(Event event) {
//				section.layout(true,true);
//				//form.getShell().pack(true);
//				tree.layout();
//				
//			}
//		});
//		tree.addListener(SWT.Collapse, new Listener() {
//			public void handleEvent(Event event) {
//				tree.layout(true);
//				tree.pack(true);
//				section.layout(true,true);
//				section.pack();
//			}
//		});
	}

	private String getExpandedTitle() {
		return "Locator";
	}
	
//	private TableWrapData setLayoutAdjustingForTreeHeight(Section section, Tree tree) {
//		TableWrapData td = new TableWrapData();
//		td.align = TableWrapData.FILL;
//		td.grabHorizontal = true;
//		int itemHeight = tree.getItemHeight();
//		System.out.println(itemHeight);
//		int numberOfItems = getTreeDepth(tree);
//		System.out.println(numberOfItems);
//		
////		td.heightHint = 50 + childCount*30; //cheesy but locators are getting cut off...
//		td.heightHint = 50 + numberOfItems*itemHeight;
//		
//		System.out.println(td.heightHint);
//		section.setLayoutData(td);
//		return td;
//	}


	
//	private int getTreeDepth(Tree tree) {
//		int depth = 0;
//		TreeItem item = tree.getTopItem();
//		while (item != null) {
//			++depth;
//			item = item.getItemCount() > 0 ? item.getItem(0) : null; 
//		}
//		return depth;
//	}

	private void expandAll(Tree tree) {
		
		TreeItem[] items = tree.getItems();
		expandAll(items);
	}

	private void expandAll(TreeItem[] items) {
		for (int i = 0; i < items.length; i++) {
			items[i].setExpanded(true);
			expandAll(items[i].getItems());
		}
	}



}
