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
package com.windowtester.runtime.swt.internal.abbot.matcher;

import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.util.TextUtils;

import abbot.finder.matchers.swt.AbstractMatcher;

/**
 * A matcher that matches tree items based on their path descriptions.
 * <p>
 * For example, suppose a tree consists of a parent node "parent" and a 
 * child node "child", the child would be matched by a matcher defined
 * this way:
 * <pre>
 * 	new TreeItemByPathMatcher("parent/child");
 * </pre>
 */
public class TreeItemByPathMatcher extends AbstractMatcher {

	/** The path to match */
	private final String _pathString;

	/** The current widget path */
	private String _wPathString;

	/**
	 * Create an instance.
	 * @param pathString the path to match (e.g., "parent/child") 
	 */
	public TreeItemByPathMatcher(String pathString) {
		_pathString = pathString;
	}

	/**
	 * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
	 */
	public boolean matches(Widget w) {
		if (!(w instanceof TreeItem))
			return false;
		final TreeItem item = (TreeItem) w;
		item.getDisplay().syncExec(new Runnable() {
			public void run() {
				setWPath(extractPathString(item));
			}
		});

		if (getWPath() == null)
			return false;
		if (getTargetPathString() == null)
			return getWPath() == null;
		//System.out.println("matching: " + getWPath() + " against " + getTargetPathString());
		return stringsMatch(getTargetPathString(), getWPath());
	}

	private String getWPath() {
		return _wPathString;
	}

	private String getTargetPathString() {
		return _pathString;
	}

	//Necessary because of JVM memory model requirements
	private synchronized void setWPath(String wPathString) {
		_wPathString = wPathString;
	}

	/**
	 * Create a path String that identifies this tree item with respect to its parent's (e.g. "Java/Project")
	 * @param item - the tree item
	 * @return a String representing its path
	 */
	public static String extractPathString(final TreeItem item) {
		final String extractedPath[] = new String[1];
		item.getDisplay().syncExec(new Runnable(){
			public void run() {
				String path = TextUtils.escapeSlashes(item.getText());
				//handle dummy child case (approximate?)
				if (path == "") {
					TreeItem parentItem = item.getParentItem();
					if (parentItem != null)
						if (!parentItem.getExpanded()) {
							extractedPath[0] = "";
							return;
						}
				}
					
				for (TreeItem parent = item.getParentItem(); parent != null; parent = parent
						.getParentItem()) {
					//prepend 
					path = TextUtils.escapeSlashes(parent.getText()) + '/' + path;
				}
				extractedPath[0] = path;
			} 			
		});
		return extractedPath[0];
	}
}
