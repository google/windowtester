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

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TreeItem;
import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;

/**
 * A specialized {@link SWTWidgetLocation} for {@link TreeItem} that understands columns
 * within a tree.
 */
public class SWTTreeItemLocation extends SWTWidgetLocation<TreeItemReference>
{
	private int columnIndex = -1;

	/**
	 * Construct a new instance representing a location relative to the specified
	 * {@link TreeItem}
	 * 
	 * @param treeItemRef the reference to the tree item to which the location is relative
	 *            (not <code>null</code>)
	 * @param relative how the the location is relative to the widget's bounding box (
	 *            {@link WTInternal#TOPLEFT}, {@link WTInternal#RIGHT}, ...)
	 */
	public SWTTreeItemLocation(TreeItemReference treeItemRef, int relative) {
		super(treeItemRef, relative);
	}

	/**
	 * Construct a new instance representing a location relative to the specified tree
	 * item with a default location offset 5 pixels in both dimensions from the widget's
	 * top left corner.
	 * 
	 * @param item the reference to the tree item to which the location is relative (not
	 *            <code>null</code>)
	 * @param click the click description (not <code>null</code>)
	 */
	public static SWTTreeItemLocation withDefaultTopLeft33(final TreeItemReference item, IClickDescription click) {
		SWTTreeItemLocation loc = new SWTTreeItemLocation(item, WTInternal.TOPLEFT);
		if (!click.isDefaultCenterClick())
			loc.offset(click.x(), click.y());
		else
			loc.offset(3, 3);
		return loc;
	}

	/**
	 * The index of the column in the tree to which the location is relative
	 * 
	 * @param columnIndex the tree column index or -1 if the location is relative to the
	 *            table item and not to any column in the table item.
	 * @return this object so that calls can be cascaded on a single line such as
	 * 
	 *         <code>new SWTTreeItemLocation(treeItem, WTInternal.RIGHT).at(-8, 0).location();</code>
	 */
	public SWTTreeItemLocation column(int columnIndex) {
		this.columnIndex = columnIndex;
		return this;
	}

	//=======================================================================
	// Internal

	/**
	 * Calculate the client area of the widget and convert that from local coordinates to
	 * global coordinates (also known as display coordinates).
	 * 
	 * @return the client area of the widget in display coordinates
	 */
	protected Rectangle getDisplayBounds() {
		return getWidgetRef().getDisplayBounds(columnIndex);
	}

	public String toString() {
		return getClass().getName() + "{" + relative + "," + columnIndex + "," + widgetRef + "}";
	}
}
