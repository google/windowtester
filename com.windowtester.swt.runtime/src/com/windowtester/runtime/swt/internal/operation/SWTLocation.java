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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.provisional.WTInternal;

/**
 * A global screen location described in terms of widgets and/or x/y coordinates. If no
 * widgets are specified, then the x/y coordinates are considered to be global screen
 * coordinates. If one widget is specified, then the x/y coordinates are considered to be
 * relative to that widget. Multiple widgets need only be specified if SWT returns bounds
 * for a widget in terms if its parent rather than in terms of global screen coordinates.
 */
public abstract class SWTLocation
{
	private static final Point ZERO_ZERO = new Point(0, 0);
	
	protected final int relative;
	private Point offset = ZERO_ZERO;

	/**
	 * Construct a new instance representing a location relative to a widget
	 * {@link SWTTreeItemLocation} or to the display itself {@link SWTDisplayLocation}
	 * 
	 * @param relative how the the location is relative to the widget's or display's
	 *            bounding box ( {@link WTInternal#TOPLEFT}, {@link WTInternal#RIGHT},
	 *            ...)
	 */
	protected SWTLocation(int relative) {
		this.relative = relative;
	}

	/**
	 * Set the location x/y coordinates. If a widget is specified via
	 * {@link #on(Widget, int)}, then the x, y coordinates specified here are treated as
	 * offsets from the specified widget location. If no widget is specified via
	 * {@link #on(Widget, int)}, then the x, y coordinates specified here are treated as
	 * global coordinates.
	 * 
	 * @param x the location's x-coordinate
	 * @param y the location's y-coordinate
	 * @return this object so that calls can be cascaded on a single line such as
	 *         <code>new SWTLocation().on(widget, WTInternal.RIGHT).at(-8, 0).location();</code>
	 */
	public SWTLocation offset(int x, int y) {
		return offset(new Point(x, y));
	}

	/**
	 * Set the location x/y coordinates. If a widget is specified via
	 * {@link #on(Widget, int)}, then the x, y coordinates specified here are treated as
	 * offsets from the specified widget location. If no widget is specified via
	 * {@link #on(Widget, int)}, then the x, y coordinates specified here are treated as
	 * global coordinates.
	 * 
	 * @param pt the coordinates or <code>null</code>
	 * @return this object so that calls can be cascaded on a single line such as
	 *         <code>new SWTLocation().on(widget, WTInternal.RIGHT).at(-8, 0).location();</code>
	 */
	public SWTLocation offset(Point pt) {
		offset = pt;
		return this;
	}

	/**
	 * Answer the global point for the specified widget(s) and offset. This method is
	 * designed to be called from the UI thread and may throw an exception if called from
	 * a non-UI thread.
	 */
	public Point location() {
		Rectangle bounds = getDisplayBounds();

		int x;
		if ((relative & WTInternal.LEFT) == WTInternal.LEFT)
			x = bounds.x;
		else if ((relative & WTInternal.RIGHT) == WTInternal.RIGHT)
			x = bounds.x + bounds.width;
		else
			x = bounds.x + (bounds.width / 2);

		int y;
		if ((relative & WTInternal.TOP) == WTInternal.TOP)
			y = bounds.y;
		else if ((relative & WTInternal.BOTTOM) == WTInternal.BOTTOM)
			y = bounds.y + bounds.height;
		else
			y = bounds.y + (bounds.height / 2);

		if (offset != null) {
			x += offset.x;
			y += offset.y;
		}

		return new Point(x, y);
	}

	//=======================================================================
	// Internal

	/**
	 * Calculate the client area of the widget and convert that from local coordinates to
	 * global coordinates (also known as display coordinates). This method is
	 * designed to be called from the UI thread and may throw an exception if called from
	 * a non-UI thread.
	 * 
	 * @return the client area of the widget in display coordinates
	 */
	protected abstract Rectangle getDisplayBounds();
}
