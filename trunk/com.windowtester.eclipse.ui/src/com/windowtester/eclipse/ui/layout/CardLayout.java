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
package com.windowtester.eclipse.ui.layout;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class CardLayout extends Layout
{
	/**
	 * The maximum of the widths of all of the children.
	 */
	protected int maxWidth = -1;

	/**
	 * The maximum of the heights of all of the children.
	 */
	protected int maxHeight = -1;

	/**
	 * The composite for whom this layout lays out the children.
	 */
	protected Composite owner;

	/**
	 * The identifier of the child that was last made visible.
	 */
	protected String visibleChildId;

	////////////////////////////////////////////////////////////////////////////
	//
	// Constructors
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Initialize a newly created layout for the given owner.
	 *
	 * @param owner the composite whose controls will be layed out by this layout
	 */
	public CardLayout(Composite owner)
	{
		this.owner = owner;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Visibility Control
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Cause the child of the owning composite with the given identifier to be
	 * made visible and the previously visible control to be hidden.
	 *
	 * @param identifier the identifier of the control to be made visible
	 */
	public void show(String identifier)
	{
		Control[] children;
		int oldIndex, newIndex;
		
		if (owner.isDisposed())
			return;
		
		children = owner.getChildren();
		oldIndex = indexOfVisibleChild(children);
		visibleChildId = identifier;
		newIndex = indexOfVisibleChild(children);
		if (newIndex >= 0 && newIndex != oldIndex) {
			children[newIndex].setVisible(true);
			if (oldIndex >= 0) {
				children[oldIndex].setVisible(false);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Layout Methods
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Computes and returns the size of the specified composite's client area
	 * according to this layout.
	 * <p>
	 * This method computes the minimum size that the client area of the
	 * composite must be in order to position all children at their minimum
	 * size inside the composite according to the layout algorithm encoded by
	 * this layout.
	 * <p>
	 * When a width or height hint is supplied, it is used to constrain the
	 * result. For example, if a width hint is provided that is less than the
	 * minimum width of the client area, the layout may choose to wrap and
	 * increase height, clip, overlap, or otherwise constrain the children.
	 *
	 * @param composite a composite widget using this layout
	 * @param wHint width (SWT.DEFAULT for minimum)
	 * @param hHint height (SWT.DEFAULT for minimum)
	 * @param flushCache true means flush cached layout values
	 *
	 * @return a point containing the computed size (width, height)
	 */
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache)
	{
		int width, height;

		initialize(composite, flushCache);
		width = wHint;
		height = hHint;
		if (wHint == SWT.DEFAULT) {
			width = maxWidth;
		}
		if (hHint == SWT.DEFAULT) {
			height = maxHeight;
		}
		return new Point(width, height);
	}

	/**
	 * Lays out the children of the specified composite according to this layout.
	 * <p>
	 * This method positions and sizes the children of a composite using the
	 * layout algorithm encoded by this layout. Children of the composite are
	 * positioned in the client area of the composite. The position of the
	 * composite is not altered by this method.
	 * <p>
	 * When the flush cache hint is true, the layout is instructed to flush any
	 * cached values associated with the children. Typically, a layout will cache
	 * the preferred sizes of the children to avoid the expense of computing
	 * these values each time the widget is layed out.
	 * <p>
	 * When layout is triggered explicitly by the programmer the flush cache hint
	 * is true. When layout is triggered by a resize, either caused by the
	 * programmer or by the user, the hint is false.
	 *
	 * @param composite a composite widget using this layout
	 * @param flushCache <code>true</code> means flush cached layout values
	 */
	protected void layout(Composite composite, boolean flushCache)
	{
		Rectangle clientArea;
		Control[] children;
		int visibleIndex;

		clientArea = composite.getClientArea();
		children = composite.getChildren();
		visibleIndex = indexOfVisibleChild(children);
		for (int i = 0; i < children.length; i++) {
			children[i].setBounds(clientArea);
			children[i].setVisible(i == visibleIndex);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Utilities
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Initialize the maximum width and height from the given composite if they
	 * have not already been computed or if the given flag is <code>true</code>.
	 *
	 * @param composite a composite widget using this layout
	 * @param flushCache <code>true</code> means flush cached layout values
	 */
	protected void initialize(Composite composite, boolean flushCache)
	{
		Control[] children;
		Point size;

		if (flushCache || maxWidth < 0 || maxHeight < 0) {
			children = composite.getChildren();
			maxWidth = 0;
			maxHeight = 0;
			for (int i = 0; i < children.length; i++) {
				size = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
				maxWidth = Math.max(maxWidth, size.x);
				maxHeight = Math.max(maxHeight, size.y);
			}
		}
	}

	/**
	 * Return the index of the visible child in the given list of children.
	 * If the visible child has not yet been initialized, or if it is no
	 * longer in the list, then make the first control in the list be the
	 * visible child. If the list is empty, return <code>-1</code>.
	 *
	 * @param children the list of children
	 *
	 * @return the index of the visible child
	 */
	protected int indexOfVisibleChild(Control[] children)
	{
		if (visibleChildId != null) {
			for (int i = 0; i < children.length; i++) {
				if (visibleChildId.equals(children[i].getLayoutData())) {
					return i;
				}
			}
		}
		if (children.length > 0) {
			visibleChildId = (String) children[0].getLayoutData();
			return 0;
		}
		return -1;
	}
}