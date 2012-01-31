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
package com.windowtester.runtime.swt.internal.reveal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.TreeTester;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.abbot.TreeItemTester;
import com.windowtester.runtime.swt.internal.selector.TreeItemSelector;
import com.windowtester.runtime.swt.internal.util.TextUtils;

/**
 * A custom revealer for revealing tree items.
 */
public class TreeRevealer implements IRevealStrategy {

	/** UI action helpers */
	private final TreeItemTester _treeItemTester = new TreeItemTester();
	private final TreeTester _treeTester = new TreeTester();
	private final TreeItemSelector _selector = new TreeItemSelector();

	/**
	 * @see com.windowtester.runtime.swt.internal.reveal.IRevealStrategy#reveal(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public Widget reveal(Widget target, int x, int y) {
		//TODO: more smarts here -- for now, no-op
		return target;
	}
	
	/**
	 * @see com.windowtester.runtime.swt.internal.reveal.IRevealStrategy#reveal(org.eclipse.swt.widgets.Widget, java.lang.String, int, int)
	 */
	public Widget reveal(Widget w, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		Tree tree = null;
		if (w instanceof Tree)
			tree = (Tree)w;
		else if (w instanceof TreeItem)
			tree = _treeItemTester.getParent((TreeItem)w);
		
		TreeItem node = findNode(tree, path);
		//if node wasn't findable we may need to dynamically navigate the tree and select
		if (node == null) {
			return doClick(tree, path);
		} else {
			//try and reveal using low-level show:
			_treeTester.showItem(tree, node);
			return node;
		}
		
	}

	/**
	 * Perform the click to select operation.
	 */
	private Widget doClick(Tree tree, String path) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		return _selector.click(tree, path, SWT.BUTTON1);
	}

	/**
	 * Find a tree item with this path description in the given tree.
	 */
	private TreeItem findNode(final Tree tree, final String path) {
		
		final TreeItem[] node = new TreeItem[1];
		
		tree.getDisplay().syncExec(new Runnable() {
			public void run() {
				TreeItem[] items = _treeTester.getItems(tree);
				for (int i = 0; i < items.length; i++) {
					if (path.equals(extractPathString(items[i]))) {
						node[0] = items[i];
						return;
					}
				}				
			}
		});
		
		return node[0];
	}

	
	/**
	 * Create a path String that identifies this tree item with respect to its parent's (e.g. "Java/Project")
	 * @param item - the tree item
	 * @return a String representing its path
	 */
	private static String extractPathString(TreeItem item) {
		String path = TextUtils.escapeSlashes(item.getText());
		for (TreeItem parent = item.getParentItem(); parent != null; parent = parent
				.getParentItem()) {
			//prepend
			path = parent.getText() + '/' + path;
		}
		return path;
	}

}
