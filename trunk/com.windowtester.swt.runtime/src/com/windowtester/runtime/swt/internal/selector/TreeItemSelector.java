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
package com.windowtester.runtime.swt.internal.selector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.matchers.swt.TextMatcher;
import abbot.script.Condition;
import abbot.tester.swt.TreeTester;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.abbot.TreeItemTester;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.util.PathStringTokenizerUtil;

/**
 * A Selector for Tree Items.
 * 
 * This is a legacy class preserved for use in old UIContext calls.
 * 
 * Moving forward, prefer {@link TreeItemSelector2}
 * 
 * @deprecated
 * 
 */
public class TreeItemSelector extends BasicWidgetSelector {
		
	private TreeTester _treeTester         = new TreeTester();
	private TreeItemTester _treeItemTester = new TreeItemTester();

	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#clickExpand(org.eclipse.swt.widgets.Widget)
	 */
	public Widget clickExpand(Widget w) {
		TreeItem item      = (TreeItem)w;
		Rectangle bounds   = UIProxy.getBounds(item);
		boolean isExpanded = UIProxy.getExpanded(item);
		
		//TODO: solve this more robustly
		int x = Math.round(bounds.x/4);
		int y = Math.round(bounds.y/2);
		click(w,-x,y, SWT.BUTTON1);
		
		UIDriver.wait(new TreeItemExpandedCondition(item, !isExpanded), 20000);
		return w;
	}    
    
	/**
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#click(org.eclipse.swt.widgets.Widget, java.lang.String, int)
	 */
	public Widget click(Widget w, String path, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		if (w instanceof Tree)
			return click((Tree)w, path, mask);
		//other cases?
		return null;
	}
	
	/**
	 * Click the Tree Item described by this path in the given tree/
	 * @param tree - the parent tree
	 * @param path - the path to the tree item in question
	 * @param mask - the mouse mask
	 * @return the clicked Tree Item
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 */
	public Widget click(Tree tree, String path, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		Widget clicked = null;
		try {		
			//find item
			TreeItem item = find(tree, path);
			//reveal item
			_treeTester.showItem(tree, item);
			// it make sense to dispatch system events at this point
			waitForIdle(tree.getDisplay());
			if(SWT.getPlatform().equals("gtk"))   // no harmful for Windows
				showItemExt(tree, item);
			//and click it
			Rectangle rect = UIProxy.getBounds(item);
			clicked = click (item, 1, rect.height/2, mask);
		} catch(IllegalStateException e) {
			//in this case, fall back on abbot:
			try {
				clicked = _treeItemTester.actionClickTreeItem(path, tree, 1);
				/*
				 * Extra provision to ensure checks happen.
				 * NOTE: This began as a fix for Linux but appears to be relevant in the win32 case
				 * as well (com.collab.wt.smoke.tests.TreeCheckTest was failing)
				 */
				if ((mask & SWT.CHECK) == SWT.CHECK) {
					setChecked(clicked);
				}

			} catch (abbot.finder.swt.WidgetNotFoundException e1) {
				throw new WidgetNotFoundException("Path: " + path + " not findable in tree " + UIProxy.getToString(tree));
			} catch (abbot.finder.swt.MultipleWidgetsFoundException e1) {
				throw new MultipleWidgetsFoundException("Path: " + path + " ambiguous in tree " + UIProxy.getToString(tree));
			} catch(IllegalStateException e2) {
				LogHandler.log(e2);
			}
		}
		return clicked;
	}
    
	public Widget doubleClick(Widget w, String path, int mask) {
		Tree tree = (Tree)w;
		Widget clicked = null;
		try {		
			//find item
			TreeItem item = find(tree, path);
			//reveal item
			_treeTester.showItem(tree, item);
			//it make sense to dispatch system events at this point
			waitForIdle(tree.getDisplay());
			if(SWT.getPlatform().equals("gtk"))   // no harmful for Windows
				showItemExt(tree, item);
			//and click it
			Rectangle rect = UIProxy.getBounds(item);
			clicked = doubleClick (item, 1, rect.height/2, mask);
		} catch(IllegalStateException e) {
			//in this case, fall back on abbot:
			try {
				clicked = _treeItemTester.actionClickTreeItem(path, tree, 2);
			} catch (abbot.finder.swt.WidgetNotFoundException e1) {
				throw new IllegalStateException("Path: " + path + " not findable in tree " + UIProxy.getToString(tree));
			} catch (abbot.finder.swt.MultipleWidgetsFoundException e1) {
				throw new IllegalStateException("Path: " + path + " ambiguous in tree " + UIProxy.getToString(tree));
			} catch(IllegalStateException e2) {
				LogHandler.log(e2);
			}
		}
		return clicked;
	}
	
	
	/**
	 * @throws WidgetNotFoundException 
	 * @throws MultipleWidgetsFoundException 
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#contextClick(org.eclipse.swt.widgets.Widget, java.lang.String, java.lang.String)
	 */
//	public Widget contextClick(Widget w, String itemPath, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		Tree tree = (Tree)w;
//		Widget clicked = null;
//		if (itemPath == null || itemPath.length() == 0)
//			clicked = tree;
//		else
//		{
//			try {		
//				//find item
//				TreeItem item = find(tree, itemPath);
//				//reveal item
//				_treeTester.showItem(tree, item);
//				//it make sense to dispatch system events at this point
//				waitForIdle(tree.getDisplay());
//				clicked = item;
//			} catch(IllegalStateException e) {
//				//in this case, fall back on abbot:
//				try {
//					clicked = _treeItemTester.getTreeItemByPath(itemPath, tree);
//				} catch (abbot.finder.swt.WidgetNotFoundException e1) {
//					//close menu in case of failure
//					handleMenuClose();
//					//throw new WidgetNotFoundException("Tree Item: " + itemPath + " not findable in tree " + UIProxy.getToString(tree));
//					clicked = tree;
//				} catch (abbot.finder.swt.MultipleWidgetsFoundException e1) {
//					//close menu in case of failure
//					handleMenuClose();
//					throw new MultipleWidgetsFoundException("Tree Item: " + itemPath + " ambiguous in tree " + UIProxy.getToString(tree));
//				} catch(IllegalStateException e2) {
//					LogHandler.log(e2);
//				}
//			}
//		}
//		return contextClick(clicked, menuPath);
//	}
	
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#selectAll(org.eclipse.swt.widgets.Widget)
	 */
	public void selectAll(final Widget w) {
		w.getDisplay().syncExec(new Runnable() {
			public void run() {
				((Tree)w).selectAll();
			}
		});
	}
	
	/**
	 * Find the Tree Item described by this path String.
	 * @param tree - the containing tree
	 * @param path - the path describing the item in question
	 * @return the found Tree Item
	 */
	private TreeItem find(Tree tree, String path) {
	
		//Fixing to handle escaped '\'s
		//String[] nodeLabels = path.split(DELIM);
		String[] nodeLabels = PathStringTokenizerUtil.tokenize(path);
		
		TreeItem[] items;
		TreeItem node = null, current = null;
		
		items = _treeTester.getItems(tree);
	
		//for each node label
		for (int i = 0; i < nodeLabels.length; ++i) {
			String nodeLabel = nodeLabels[i];
			node = null;
			//find the appropriate item
			for (int j = 0; j < items.length && node == null; j++) {
				current = items[j];
				if (new TextMatcher(nodeLabel).matches(current)) {
					node = current;
				}
			}
			if (node == null)
				throw new IllegalStateException("no matching node found"); //TODO: wrap this in a WidgetNotFoundException
			//get its children
			items = _treeItemTester.getItems(node);
		}
		return node;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Wait condition helpers
	//
	////////////////////////////////////////////////////////////////////////////
    
    /**
     * A condition that waits for an expanded (true or false) condition.
     */
    public class TreeItemExpandedCondition implements Condition {
        private final TreeItem _treeNode;
		private final boolean _isExpanded;
 
        /**
         * Create an instance.
         * @param treeNode - the node to wait on
         * @param isExpanded - whether the node should be expanded or not
         */
        public TreeItemExpandedCondition(TreeItem treeNode, boolean isExpanded) {
        	_treeNode   = treeNode;
			_isExpanded = isExpanded;
        }

        /**
         * @see abbot.script.Condition#test()
         */
        public boolean test() {
        	return UIProxy.getExpanded(_treeNode) == _isExpanded;
        }
    }

    public void showItemExt(final Tree t, final TreeItem item) {
    	if (t == null || item == null)
    		return;

    	Rectangle itemRect = _treeItemTester.getBounds(item);
    	setValueToScrollBar(_treeTester.getHorizontalBar(t), itemRect.x);
    }

    public void setValueToScrollBar(final ScrollBar bar, final int value)
    {
    	bar.getDisplay().syncExec(new Runnable() {
            public void run() {
            	bar.setSelection(value);
    			waitForIdle(bar.getDisplay());
            }
        });
    }

}