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
package com.windowtester.runtime.swt.internal.finder;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;

import abbot.tester.swt.RunnableWithResult;

import com.windowtester.runtime.swt.internal.selector.UIProxy;

/**
 * Helper for accessing filtered trees.
 */
public class FilteredTreeHelper {

	
	public static boolean isItemInFilteredTree(Object widget) {
		if (!(widget instanceof TreeItem))
			return false;
		return containedInFilteredTree((TreeItem)widget);
	}

	public static boolean containedInFilteredTree(final TreeItem item) {
		/* $codepro.preprocessor.if version >= 3.2.0 $ */ 
		//batch this in one UI thread access
		return ((Boolean)UIProxy.syncExec(item.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				Tree tree = item.getParent();
				Composite parent = tree.getParent();
				while (parent != null) {
					if (parent instanceof FilteredTree)
						return Boolean.TRUE;
					parent = parent.getParent();
				}
				return Boolean.FALSE;
			}
		})).booleanValue();	
		/* $codepro.preprocessor.endif$ */
		
		/* $codepro.preprocessor.if version < 3.2.0 $
		return false;
		$codepro.preprocessor.endif$ */
	}
	
}
