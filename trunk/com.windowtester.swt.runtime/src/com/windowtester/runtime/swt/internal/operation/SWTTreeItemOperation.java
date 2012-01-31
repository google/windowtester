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
package com.windowtester.runtime.swt.internal.operation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;

/**
 * Perform operations on tree items such as expanding, collapsing, or checking by checking
 * the tree item state and issuing mouse or key commands a click or drag operation by
 * pushing mouse move, mouse down, and mouse up operations onto the OS event queue. The
 * tree item is programmatically shown before any expanding, collapsing, or checking
 * events occur.
 */
public class SWTTreeItemOperation extends SWTWidgetOperation<TreeItemReference>
{
	/*
	 * TODO columns can be reordered... so what does column index represent?
	 * If the columns have not been reordered, then the visual arrangement
	 * of columns is the same as the order in which they were created.
	 * See {@link Tree#getColumnOrder()}
	 */

	private final int columnIndex;

	/**
	 * Construct a new instance to manipulate the specified tree item
	 * 
	 * @param treeItem the tree item (not <code>null</code>)
	 */
	public SWTTreeItemOperation(TreeItemReference treeItem) {
		this(treeItem, -1);
	}

	/**
	 * Construct a new instance to manipulate the specified tree item
	 * 
	 * @param treeItemRef the reference to the tree item (not <code>null</code>)
	 * @param columnIndex the index of the column in the tree item relative to which the
	 *            operation should occur or -1 if the operation should occur relative to
	 *            the entire tree item
	 */
	public SWTTreeItemOperation(TreeItemReference treeItemRef, int columnIndex) {
		super(treeItemRef);
		this.columnIndex = columnIndex;
	}

	/**
	 * Programmatically show the tree item before any other actions occur
	 * 
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTTreeItemOperation(treeItem, -1).show().execute();</code>
	 */
	public SWTTreeItemOperation show() {
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				TreeItem treeItem = getWidgetRef().getWidget();
				Tree parent = treeItem.getParent();
				parent.showItem(treeItem);
				if (SWTTreeItemOperation.this.columnIndex >= 0)
					parent.showColumn(parent.getColumn(SWTTreeItemOperation.this.columnIndex));
			}
		});
		return this;
	}

	/**
	 * Show then expand the tree item, waiting for child elements to be visible.
	 * 
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTTreeItemOperation(treeItem, -1).show().execute();</code>
	 */
	public SWTTreeItemOperation expand() {
		show();
		queueStep(null);
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				TreeItem treeItem = getWidgetRef().getWidget();
				if (!treeItem.getExpanded()) {
					// Click the expand toggle to expand the tree item
					int button = getButton(WT.BUTTON1);
					Point pt = new SWTTreeItemExpandChevronLocation(getWidgetRef()).location();

					// Linux needs a wiggle before a click
					queueMouseWiggle(pt);
					
					queueMouseDown(button, pt);
					queueMouseUp(button, pt);

					// Linux needs a wiggle before a click
					queueMouseWiggle(pt);
					
					queueStep(null);
				}
				
				// Apparently TreeItem#getExpanded() always returns false if the item has no children
				// So we can get permanently stuck here... 
//				queueStep(new Step() {
//					public void executeInUI() throws Exception {
//						TreeItem treeItem = getWidgetRef().getWidget();
//						if (!treeItem.getExpanded())
//							throw new SWTOperationStepException("Tree item " + treeItem + " has not yet expanded");
//					}
//				});
			}
		});
		return this;
	}

	/**
	 * Show then check the tree item's checkbox
	 * 
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTTreeItemOperation(treeItem, -1).show().execute();</code>
	 */
	public SWTTreeItemOperation check() {
		queueAssertHasStyle(getWidgetRef().getParent(), SWT.CHECK);
		show();
		queueStep(null);
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				// Click the checkbox to the left of the tree item
				int button = getButton(WT.BUTTON1);
				Point pt = new SWTTreeItemCheckLocation(getWidgetRef()).location();

				// Linux needs a wiggle before a click
				queueMouseWiggle(pt);
				
				queueMouseDown(button, pt);
				queueMouseUp(button, pt);

				// Linux needs a wiggle after a click
				queueMouseWiggle(pt);
			}
		});
		return this;
	}

	//=======================================================================
	// Alternate methods

	// TODO [Dan] remove these unused methods or switch them with the methods above
	// once we determine which approach is better

	/**
	 * Expand the tree item programmatically with SWTBot style widget events
	 * 
	 * @deprecated
	 */
	public SWTTreeItemOperation expandSWTBotStyle() {
		queueAssertIsEnabled();
		queueWidgetEvent(SWT.Expand);
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				getWidgetRef().getWidget().setExpanded(true);
			}
		});
		queueWidgetEvent(SWT.MouseMove);
		queueWidgetEvent(SWT.Activate);
		queueWidgetEvent(SWT.FocusIn);
		queueWidgetEvent(SWT.MouseDown);
		queueWidgetEvent(SWT.MeasureItem);
		queueWidgetEvent(SWT.Deactivate);
		queueWidgetEvent(SWT.FocusOut);
		return this;
	}

	/**
	 * Check the tree item programmatically with SWTBot style widget events
	 * 
	 * @deprecated
	 */
	public SWTTreeItemOperation checkSWTBotStyle() {
		queueAssertIsEnabled();
		queueAssertHasStyle(getWidgetRef().getParent(), SWT.CHECK);
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				final TreeItem treeItem = getWidgetRef().getWidget();
				boolean isChecked = treeItem.getChecked();
				treeItem.setChecked(!isChecked);
			}
		});
		//CHECK events need to get sent to the parent to be picked up by viewers
	
		Tree parent   = getWidgetRef().getParent().getWidget();
		TreeItem item = getWidgetRef().getWidget();
		queueWidgetEvent(parent, item, SWT.Selection, SWT.CHECK);
		return this;
	}
}
