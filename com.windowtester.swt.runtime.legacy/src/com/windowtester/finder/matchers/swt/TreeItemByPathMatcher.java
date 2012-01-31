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
package com.windowtester.finder.matchers.swt;


/**
 * A matcher that matches tree items based on their path descriptions.
 * <p>
 * For example, suppose a tree consists of a parent node "parent" and a 
 * child node "child", the child would be matched by a matcher defined
 * this way:
 * <pre>
 * 	new TreeItemByPathMatcher("parent/child");
 * </pre>
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 * @author Phil Quitslund
 *
 */
public class TreeItemByPathMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.TreeItemByPathMatcher {

	public TreeItemByPathMatcher(String pathString) {
		super(pathString);
	}
}
