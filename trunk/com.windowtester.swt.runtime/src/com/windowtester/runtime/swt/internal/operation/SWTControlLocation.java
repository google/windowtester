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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.provisional.WTInternal;

/**
 * A specialized {@link SWTLocation} for {@link Control}
 * 
 * @deprecated Use {@link SWTWidgetLocation} rather than this class
 */
public class SWTControlLocation extends SWTLocation
{
	private final Control control;

	/**
	 * Construct a new instance representing a location relative to the specified
	 * {@link TreeItem}
	 * 
	 * @param control the control to which the location is relative (not
	 *            <code>null</code>)
	 * @param relative how the the location is relative to the widget's or display's
	 *            bounding box ( {@link WTInternal#TOPLEFT}, {@link WTInternal#RIGHT},
	 *            ...)
	 */
	public SWTControlLocation(Control control, int relative) {
		super(relative);
		if (control == null)
			throw new IllegalArgumentException();
		this.control = control;
	}

	//=======================================================================
	// Internal

	/**
	 * Used for {@link SWTMouseOperation} sanity check
	 */
	Widget getWidget() {
		return control;
	}

	/**
	 * Calculate the client area of the widget and convert that from local coordinates to
	 * global coordinates (also known as display coordinates).  This method is
	 * designed to be called from the UI thread and may throw an exception if called from
	 * a non-UI thread.
	 * 
	 * @return the client area of the widget in display coordinates
	 */
	protected Rectangle getDisplayBounds() {
		return control.getDisplay().map(control.getParent(), null, control.getBounds());
	}

	public String toString() {
		return getClass().getName() + "{" + relative + "," + control + "}";
	}
}
