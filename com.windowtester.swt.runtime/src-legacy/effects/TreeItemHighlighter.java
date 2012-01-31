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
package com.windowtester.runtime.swt.internal.effects;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;
import com.windowtester.runtime.swt.internal.selector.HighlightingDriver;
import com.windowtester.runtime.swt.internal.selector.UIProxy;

/**
 * A highlighter for tree items that first highlights the root and then the child nodes.
 * 
 * @author Phil Quitslund
 */
public class TreeItemHighlighter implements IHighlighter {

	private final TreeItem _treeItem;
	private final NodeItemHighlighterDelegate _nodeHighlighter;
	private final RootItemHighlighterDelegate _rootHighLighter;
	
	private Tree _parent;
	
	
	/**
	 * Create an instance.
	 * @param treeItem
	 * @param settings 
	 * @param driver 
	 */
	public TreeItemHighlighter(TreeItem treeItem, PlaybackSettings settings) {
		_treeItem = treeItem;
		_rootHighLighter = new RootItemHighlighterDelegate(getParent(), settings);
		_nodeHighlighter = new NodeItemHighlighterDelegate(getParent(), settings);
	}

	protected Tree getParent() {
		if (_parent == null) {
			final Tree[] control = new Tree[1];
			//needs to happen in the UI thread
			_treeItem.getDisplay().syncExec( new Runnable() {
				public void run() {
					control[0] = _treeItem.getParent();
				}
			});
			_parent = control[0];
		}
		return _parent;
	}
	
	
	/**
	 * @see com.windowtester.runtime.swt.internal.effects.IHighlighter#doPaint(com.windowtester.runtime.swt.internal.selector.HighlightingDriver)
	 */
	public void doPaint(HighlightingDriver driver) {
		_rootHighLighter.doPaint(driver);
		_nodeHighlighter.doPaint(driver);
	}
	

	
	class NodeItemHighlighterDelegate extends AbstractControlHighlighter {
	
		/**
		 * Create an instance.
		 * @param control
		 */
		public NodeItemHighlighterDelegate(Control parent, PlaybackSettings settings) {
			super(parent, settings);
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.effects.AbstractControlHighlighter#getPixelBuffer()
		 */
		protected int getPixelBuffer() {
			return 1;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.effects.AbstractControlHighlighter#calculateBoundingBox()
		 */
		protected Rectangle calculateBoundingBox() {
			return _treeItem.getBounds();
		}
	}

	class RootItemHighlighterDelegate extends AbstractControlHighlighter {
		
		/**
		 * Create an instance.
		 * @param control
		 */
		public RootItemHighlighterDelegate(Control parent, PlaybackSettings settings) {
			super(parent, settings);
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.effects.AbstractControlHighlighter#getPixelBuffer()
		 */
		protected int getPixelBuffer() {
			return 1;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.effects.AbstractControlHighlighter#calculateBoundingBox()
		 */
		protected Rectangle calculateBoundingBox() {
			return findRootNode(_treeItem).getBounds();
		}

		private TreeItem findRootNode(TreeItem item) {
			TreeItem found = item;
			do {
				item = found;
				found = UIProxy.getParentItem(item);
			} while (found != null);
			return item;
		}
	}
	
	
}
