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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;

/**
 * A specialized location for the expand toggle on tree items.
 */
public class SWTTreeItemCheckLocation extends SWTTreeItemLocation {
 
	
	public SWTTreeItemCheckLocation(TreeItemReference item) {
		super(item, WTInternal.LEFT);
		if (!OS.isOSX())
			offset(checkOffset(item));
	}

	private Point checkOffset(TreeItemReference item) {
		Point pt = new Point(-5, 0);

		// If the tree item has an image, then the expand toggle is further left
		Image image = item.getImage();
		if (image != null)
			pt.x -= (image.getBounds().width); 
		return pt;		
	}

	
	@Override
	public Point location() {
		
		if (OS.isOSX()){
		
		TreeItemReference item = getWidgetRef();
		TreeReference tree = item.getParent();
		Rectangle treeBounds = tree.getDisplayBounds();
		Rectangle itemBounds = item.getDisplayBounds();
		// TODO[pq]: add padding for borders?
		Point pt = new Point(treeBounds.x + 14, itemBounds.y + itemBounds.height/2);
		// TODO[pq]: another approach would be to calculate offset based on the x coord of any top-level item with the y of the current
		return pt;
		}
		return super.location();
	}
	
	
}
