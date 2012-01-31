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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.provisional.WTInternal;

/**
 * A location relative to the display
 * @deprecated Every location *should* be relative to a widget. Better to be relative to
 *             the window/shell than the display. Switch to using {@link SWTWidgetLocation}
 */
public class SWTDisplayLocation extends SWTLocation
{
	private final Display display;

	/**
	 * Construct an instance relative to the top left corner of the default display
	 */
	public SWTDisplayLocation() {
		this(Display.getDefault());
	}

	/**
	 * Construct an instance relative to the top left corner of the specified display
	 * 
	 * @param display the display to which this location is relative
	 */
	public SWTDisplayLocation(Display display) {
		this(display, WTInternal.TOPLEFT);
	}

	/**
	 * Construct an instance relative to the top left corner of the specified display
	 * 
	 * @param display the display to which this location is relative
	 * @param relative how the the location is relative to the widget's or display's
	 *            bounding box ( {@link WTInternal#TOPLEFT}, {@link WTInternal#RIGHT},
	 *            ...)
	 */
	public SWTDisplayLocation(Display display, int relative) {
		super(relative);
		this.display = display;
	}

	//=======================================================================
	// Internal

	/**
	 * Used for {@link SWTMouseOperation} sanity check
	 */
	Widget getWidget() {
		return null;
	}

	/**
	 * Answer the client area of the display to which this location is relative.
	 */
	protected Rectangle getDisplayBounds() {
		return display.getBounds();
	}

	public String toString() {
		return getClass().getName() + "{" + relative + "}";
	}
}
