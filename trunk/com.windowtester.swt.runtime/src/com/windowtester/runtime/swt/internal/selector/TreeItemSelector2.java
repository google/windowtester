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

import java.awt.Point;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.matchers.swt.TextMatcher;
import abbot.script.Condition;
import abbot.tester.swt.TreeTester;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.internal.abbot.TreeItemTester;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.swt.internal.operation.SWTTreeItemLocation;
import com.windowtester.runtime.swt.internal.operation.SWTTreeItemOperation;
import com.windowtester.runtime.swt.internal.util.PathStringTokenizerUtil;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.util.ScreenCapture;

/**
 * A Selector for Tree Items.
 */
public class TreeItemSelector2 extends BasicWidgetSelector {
		
	private TreeTester treeTester         = new TreeTester();
	private TreeItemTester treeItemTester = createTreeItemTester();

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
    
	//NOTE: override at will...
	protected TreeItemTester createTreeItemTester() {
		return new TreeItemTester();
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
	
	
	public Widget click(int clicks, Tree tree, String path, int columnIndex, Point offset, int mask) throws WidgetSearchException {
//		Widget clicked = null;
//		try {		
			//find item
			TreeItem item;
			try {
				item = atomicFind(tree, path);
			}
			catch (WidgetSearchException e) {
				// [Dan] Can't figure out where the screen capture call was so add it here. 
				// Should probably rethink this when overhauling the selector classes
				ScreenCapture.createScreenCapture();
				throw e;
			}
			//reveal item
			
//			treeTester.showItem(tree, item);
//			showColumn(tree, columnIndex);
			// it make sense to dispatch system events at this point
//			waitForIdle(tree.getDisplay());
//			if(SWT.getPlatform().equals("gtk"))   // no harmful for Windows
//				showItemExt(tree, item);
			
			//click it
//			Rectangle rect = (columnIndex ==  -1) ? UIProxy.getBounds(item) : UIProxy.getBounds(item, columnIndex);
//			//w/ offset
//			if (offset == null) 
//				offset = new Point(1, rect.height/2);
//			if (columnIndex != -1) {
//				offset.x += rect.x;
//			}
//			clicked = click (item, offset.x, offset.y, mask, clicks);

			TreeItemReference itemRef = new TreeItemReference(item);
			
			SWTTreeItemOperation treeItemOp = new SWTTreeItemOperation(itemRef, columnIndex);
	    	if ((mask & WT.CHECK) == WT.CHECK)
				treeItemOp.checkSWTBotStyle();
	    	treeItemOp.execute();
	    	
	    	if ((mask & WT.BUTTON_MASK) != 0) {
		    	SWTTreeItemLocation loc = new SWTTreeItemLocation(itemRef, WTInternal.TOPLEFT).column(columnIndex);
				if (offset != null)
					loc.offset(offset.x, offset.y);
				else
					loc.offset(5, 5);
				new SWTMouseOperation(mask).at(loc).execute();
	    	}
			return item;
			
//		} catch(IllegalStateException e) {
//			//in this case, fall back on abbot:
//			try {
//				if (offset == null) {
//					clicked = treeItemTester.actionClickTreeItem(path, tree, clicks);
//				} else {
//					//note we do the checking ourselves
//					clicked = treeItemTester.actionClickTreeItem(path, abbot.tester.swt.TreeItemTester.DEFAULT_TREEITEM_PATH_DELIMITER, tree, TreeItemTester.NONCHECKABLE, offset.x, offset.y, clicks);
//				}
//				
//				showColumn(tree, columnIndex);
//				if (columnIndex != -1) {
//					TreeItem item = (TreeItem)clicked;
//					Rectangle rect = (columnIndex ==  -1) ? UIProxy.getBounds(item) : UIProxy.getBounds(item, columnIndex);
//					//w/ offset
//					if (offset == null) 
//						offset = new Point(1, rect.height/2);
//					offset.x += rect.x;
//					clicked = click (item, offset.x, offset.y, mask, clicks);
//				}
//				
//				
//				
//				/*
//				 * Extra provision to ensure checks happen.
//				 * NOTE: This began as a fix for Linux but appears to be relevant in the win32 case
//				 * as well (com.collab.wt.smoke.tests.TreeCheckTest was failing)
//				 */
//				if ((mask & SWT.CHECK) == SWT.CHECK) {
//					setChecked(clicked);
//				}
//
//			} catch (abbot.finder.swt.WidgetNotFoundException e1) {
//				throw new WidgetNotFoundException(e1.getMessage());
//			} catch (abbot.finder.swt.MultipleWidgetsFoundException e1) {
//				throw new MultipleWidgetsFoundException(e1);			
//			} catch(IllegalStateException e2) {
//				LogHandler.log(e2);
//			}
//		}
//		return clicked;
	}

	
	private TreeItem atomicFind(final Tree tree, final String path) throws WidgetNotFoundException {
		// [Dan] Should not run on UI thread so that tree items can be properly expanded.
//		final IllegalStateException caughtException[] = new IllegalStateException[1];
//		TreeItem item = (TreeItem) DisplayExec.sync(new RunnableWithResult() {			
//			public Object runWithResult() {
//				try {
					return find(tree, path);
//				} catch (IllegalStateException e) {
//					caughtException[0] = e;
//				}
//				return null;
//			}
//		});
//		if (caughtException[0] != null)
//			throw caughtException[0];
//		return item;
	}

//	private void showColumn(Tree tree, int columnIndex)
//			throws WidgetSearchException {
//		if (columnIndex != -1) {
//			int columnCount = treeTester.getColumnCount(tree);
//			if (columnIndex >= columnCount)
//				throw new WidgetSearchException("Specified column (" + columnIndex +") out of range (count: " + columnCount + " columns)");
//			TreeColumn column = treeTester.getColumn(tree, columnIndex);
//			treeTester.showColumn(tree, column);
//		}
//	}
	
	
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
			TreeItem item = atomicFind(tree, path);
			//reveal item
			treeTester.showItem(tree, item);
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
				clicked = treeItemTester.actionClickTreeItem(path, tree, 1);
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
			TreeItem item;
			try {
				item = atomicFind(tree, path);
			}
			catch (WidgetNotFoundException e) {
				throw new IllegalStateException(e);
			}
			//reveal item
			treeTester.showItem(tree, item);
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
				clicked = treeItemTester.actionClickTreeItem(path, tree, 2);
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
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#contextClick(org.eclipse.swt.widgets.Widget, java.lang.String, java.lang.String)
	 */
//	public Widget contextClick(Widget w, String itemPath, Point offset, String menuPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		Tree tree = (Tree)w;
//		Widget clicked = null;
//		if (itemPath == null || itemPath.length() == 0)
//			clicked = tree;
//		else
//		{
//			try {		
//				//find item
//				TreeItem item = atomicFind(tree, itemPath);
//				//reveal item
//				treeTester.showItem(tree, item);
//				//it make sense to dispatch system events at this point
//				waitForIdle(tree.getDisplay());
//				clicked = item;
//			} catch(IllegalStateException e) {
//				//in this case, fall back on abbot:
//				try {
//					clicked = treeItemTester.getTreeItemByPath(itemPath, tree);
//				} catch (abbot.finder.swt.WidgetNotFoundException e1) {
//					//close menu in case of failure
//					handleMenuClose();
//					//04.03.07: this has been re-enabled -- the consequence might be that empty tree context clicks 
//					//are affected?  (in any case, this is why Alex removed this bit...)
//					//TODO: investigate context clicks on empty trees!
//					throw new WidgetNotFoundException("Tree Item: " + itemPath + " not findable in tree " + UIProxy.getToString(tree));
//					//clicked = tree;
//				} catch (abbot.finder.swt.MultipleWidgetsFoundException e1) {
//					//close menu in case of failure
//					handleMenuClose();
//					throw new MultipleWidgetsFoundException("Tree Item: " + itemPath + " ambiguous in tree " + UIProxy.getToString(tree));
//				} catch(IllegalStateException e2) {
//					LogHandler.log(e2);
//				}
//			}
//		}
//		if (offset != null)
//			return contextClick(clicked, offset.x, offset.y, menuPath);
//		return contextClick(clicked, menuPath);
//	}

		
	/**
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
//				TreeItem item = atomicFind(tree, itemPath);
//				//reveal item
//				treeTester.showItem(tree, item);
//				//it make sense to dispatch system events at this point
//				waitForIdle(tree.getDisplay());
//				clicked = item;
//			} catch(IllegalStateException e) {
//				//in this case, fall back on abbot:
//				try {
//					clicked = treeItemTester.getTreeItemByPath(itemPath, tree);
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
//	
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
	private TreeItem find(Tree tree, String path) throws WidgetNotFoundException {
	
		//Fixing to handle escaped '\'s
		//String[] nodeLabels = path.split(DELIM);
		String[] nodeLabels = PathStringTokenizerUtil.tokenize(path);
		
		TreeItem[] items;
		TreeItem node = null;
		TreeItem current = null;
		
		items = treeTester.getItems(tree);
	
		//for each node label
		int i = 0;
		while (true) {
			String nodeLabel = nodeLabels[i];
			node = null;
			//find the appropriate item
			for (int j = 0; j < items.length && node == null; j++) {
				current = items[j];
				if (new TextMatcher(nodeLabel).matches(current)) {
					node = current;
					break;
				}
			}
			if (node == null) {
				throw new WidgetNotFoundException("Failed to find match for \"" + nodeLabel + "\" in path " + path);
			}
			if (++i >= nodeLabels.length)
				break;
			// get its children... but do not call for leaf nodes
			treeItemTester.actionExpandItem(node);
			items = treeItemTester.getItems(node);
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
    public static class TreeItemExpandedCondition implements Condition {
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

    	Rectangle itemRect = treeItemTester.getBounds(item);
    	setValueToScrollBar(treeTester.getHorizontalBar(t), itemRect.x);
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