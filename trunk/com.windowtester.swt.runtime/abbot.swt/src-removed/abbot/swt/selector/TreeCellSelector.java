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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.runtime.WT;
import com.windowtester.tester.swt.TreeItemTester;


/**
 * @author Phil Quitslund
 *
 */
public class TreeCellSelector extends TreeItemSelector2 {

//		- do NOT click when traversing (this will avoid selecting intermediary cell editors)
	
		
	/* (non-Javadoc)
	 * @see com.windowtester.event.selector.swt.TreeItemSelector2#createTreeItemTester()
	 */
	protected TreeItemTester createTreeItemTester() {
		return new TreeCellTester();
	}
		
	
	protected static class TreeCellTester extends TreeItemTester {

		/* (non-Javadoc)
		 * @see com.windowtester.tester.swt.TreeItemTester#clickToSelectItemToExpand(org.eclipse.swt.widgets.TreeItem, org.eclipse.swt.graphics.Point)
		 */
		protected void clickToSelectItemToExpand(TreeItem item, Point p) {
			super.clickToSelectItemToExpand(item, p);
			deactivateCellEditor(); 
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.tester.swt.TreeItemTester#getExpandKey()
		 */
		protected int getExpandKey() {
			return super.getExpandKey();
		}
		
		private void deactivateCellEditor() {
			keyClick(WT.ESC);
		}
		
		
	}

	
	
	
}