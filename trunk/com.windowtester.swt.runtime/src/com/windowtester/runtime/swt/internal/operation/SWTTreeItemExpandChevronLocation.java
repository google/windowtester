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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;

/**
 * A specialized location for the expand toggle on tree items.
 */
public class SWTTreeItemExpandChevronLocation extends SWTTreeItemLocation {

	private static final int CHECK_ADJUSTMENT = OS.isOSX()?   5 : 14;
	private static final int INITIAL_X_OFFSET = OS.isOSX()? -10 : -5;

	public SWTTreeItemExpandChevronLocation(TreeItemReference item) {
		super(item, WTInternal.LEFT);
		offset(chevronOffset(item));
	}

	private Point chevronOffset(TreeItemReference item) {
		
		Point pt = new Point(INITIAL_X_OFFSET, 0);

		// If the tree item has an image, then the expand toggle is further left
		Image image = item.getImage();
		if (image != null)
			pt.x -= (image.getBounds().width + 5); 

		// If the tree is a checkbox tree, then the expand toggle is further left
		int treeStyle = item.getParent().getStyle();
		if ((treeStyle & SWT.CHECK) == SWT.CHECK)
			pt.x -= CHECK_ADJUSTMENT;
		return pt;
	}

	
	
}
